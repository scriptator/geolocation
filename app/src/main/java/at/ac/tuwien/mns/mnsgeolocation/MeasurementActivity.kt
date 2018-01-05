package at.ac.tuwien.mns.mnsgeolocation

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailFragment
import at.ac.tuwien.mns.mnsgeolocation.service.*
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_measurement_list.*
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import android.location.Location
import android.util.Log
import android.view.View
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.util.DisplayUtil
import at.ac.tuwien.mns.mnsgeolocation.util.DistanceUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MeasurementActivity : AppCompatActivity(), DetailFragment.OnFragmentInteractionListener {

    private val LOG_TAG = javaClass.canonicalName

    private var measurmentTimeout: Disposable? = null
    private var measuring = false
    private var currentMeasurement: Measurement? = null

    private var lastGPSLocation: Location? = null

    private var managerServiceIntent: Intent? = null
    private var managerService: ManagerService? = null
    private var mBound = false

    var progressOverlay: View? = null

    private val listItems: ArrayList<String> = ArrayList()
    private var listAdapter: ArrayAdapter<String>? = null

    private var mlsLocationService: MLSLocationService = ServiceFactory.getMlsLocationService()

    private val receiverManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.extras?.getString(ManagerService.NOTIFICATION_TYPE)
            println("msg type: " + type.toString())
            when {
                ManagerService.NOTIFICATION_TYPE_LOCATION.equals(type) -> processGPSLocationMsg(intent?.extras?.getParcelable<Location>(ManagerService.CONTENT))
                ManagerService.NOTIFICATION_TYPE_MLS_REQUEST.equals(type) -> conductMLSLocationRequest(intent?.extras?.getParcelable<GeolocationRequestParams>(ManagerService.CONTENT))
                ManagerService.NOTIFICATION_TYPE_PERM_FAILED.equals(type) -> PermissionUtil.requestRequiredPermissions(this@MeasurementActivity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        managerServiceIntent = Intent(this, ManagerService::class.java)

        setContentView(R.layout.activity_measurement_list)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { this.startMeasurement() }
        this.progressOverlay = findViewById(R.id.progress_overlay)

        val listView = findViewById<ListView>(R.id.listView) as ListView
        this.listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, listItems)
        listView.adapter = listAdapter

        startManagerService()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiverManager, IntentFilter(ManagerService.NOTIFICATION))
        bindService(managerServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_measurement_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.requestPermissionsResultFailed(requestCode, permissions, grantResults)) {
            showToast("Location permission required!", Toast.LENGTH_LONG)
        } else {
            // TODO does it make sense to start the services already here or only when the plus button is pressed?
            //managerService?.tryStartingServices()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        //TODO
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiverManager, IntentFilter(ManagerService.NOTIFICATION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiverManager)
    }

    override fun onStop() {
        super.onStop()
        unbindService(mConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(managerServiceIntent)
    }

    private fun showToast(text: String, duration: Int) {
        Toast.makeText(this, text, duration).show()
    }

    private fun startManagerService() {
        startService(managerServiceIntent)
    }

    private fun startMeasurement() {
        Log.i(LOG_TAG, "Starting measurement")
        DisplayUtil.alphaAnimation(progressOverlay, View.VISIBLE, 0.4f, 150)

        this.currentMeasurement = Measurement()
        this.measuring = true

        // start service to get cell towers and wifi access points
        managerService!!.startMLSScanner()

        this.fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        this.fab.setOnClickListener {
            Log.i(LOG_TAG, "Aborting measurement")
            this.endMeasurement()
            showToast("Measurement aborted", Toast.LENGTH_SHORT)
        }

        measurmentTimeout = Observable.timer(20, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.i(LOG_TAG, "Aborting measurement due to time out")
                    showToast("Aborting measurement due to timeout", Toast.LENGTH_LONG)
                    this.endMeasurement()
                }
    }

    private fun endMeasurement() {
        DisplayUtil.alphaAnimation(progressOverlay, View.GONE, 0f, 150)
        this.measuring = false
        this.measurmentTimeout?.dispose()

        this.fab.setImageResource(android.R.drawable.ic_input_add)
        this.fab.setOnClickListener { this.startMeasurement() }
    }

    private fun processGPSLocationMsg(location: Location?) {
        if (location == null) {
            showToast("Last location unknown", Toast.LENGTH_LONG)
        } else {
            Log.i(LOG_TAG, "GPS location update received")
            this.lastGPSLocation = location
            this.checkMeasurementCompleted()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkMeasurementCompleted() {
        if (measuring
                && lastGPSLocation != null
                && currentMeasurement != null
                && currentMeasurement!!.mlsRequestParams != null
                && currentMeasurement!!.mlsResponse != null) {

            // reset icons, remove loading overlay, ...
            this.endMeasurement()

            val m = currentMeasurement!!

            // use the last known GPS location for the measurement
            m.gpsLocation = lastGPSLocation

            // TODO refactor list to display measurements and not a string
            val msg = "Lat: " + m.gpsLocation?.latitude + ", Lon: " + m.gpsLocation?.longitude
            showToast(msg, Toast.LENGTH_LONG)
            listItems.add(msg)
            if (listAdapter != null) {
                listAdapter?.notifyDataSetChanged()
            }
            Log.i(LOG_TAG, "Measurement complete: " + currentMeasurement)

            // ----- email sending ------
            // TODO move email sending to correct place
            val cal = Calendar.getInstance()
            cal.timeInMillis = m.timestamp
            val localDate = SimpleDateFormat.getDateTimeInstance().format(cal.time)

            val filename = "measurement_" + SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(cal.time) + ".txt"
            val outputDir = applicationContext.cacheDir // context being the Activity pointer
            val outputFile = File(outputDir, filename)
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Could not create temp file for email attachment.")
                }
            }

            val glat = m.gpsLocation!!.latitude
            val glon = m.gpsLocation!!.longitude
            val mlat = m.mlsResponse!!.location!!.lat!!
            val mlon = m.mlsResponse!!.location!!.lng!!
            val distance = DistanceUtils.haversineDistance(glat, glon, mlat, mlon)

            val b = StringBuilder()
            b.append("GPS vs MLS measurement from ")
                    .append(localDate)
                    .append(":\n\n")
            b.append("GPS:\n")
                    .append("  Location: ")
                    .append(glat)
                    .append("°N / ")
                    .append(glon)
                    .append("°E\n")
                    .append("  Accuracy: ")
                    .append(m.gpsLocation?.accuracy)
                    .append(" m\n\n")
            b.append("MLS:\n")
                    .append("  Location: ")
                    .append(mlat)
                    .append("°N / ")
                    .append(mlon)
                    .append("°E\n")
                    .append("  Accuracy: ")
                    .append(m.mlsResponse?.accuracy)
                    .append(" m\n")
                    .append("  Parameters:\n")
                    .append("    Cell Towers:\n")
            for (tower in m.mlsRequestParams!!.cellTowers) {
                b.append("      - ")
                b.append(tower)
                b.append("\n")
            }
            b.append("    WIFI Access Points:\n")
            for (ap in m.mlsRequestParams!!.wifiAccessPoints) {
                b.append("      - ")
                b.append(ap)
                b.append("\n")
            }
            b.append("\nDistance: ")
                    .append(distance)
                    .append(" m")

            outputFile.writeText(b.toString())

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MNSMeasurement at " + localDate)
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello!\n\nThis email holds your measurement from " + localDate + " as an attachment.")
            // set the attachment, share via own file provider
            emailIntent.putExtra(Intent.EXTRA_STREAM, OwnFileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, outputFile))
            // give temporary permission to the folder containing the email attachment
            emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(emailIntent)
        }
    }

    private fun conductMLSLocationRequest(mlsRequest: GeolocationRequestParams?) {
        if (mlsRequest == null) {
            Log.e(LOG_TAG, "Scanning wifi or cell towers failed")
            showToast("Scanning wifi or cell towers failed", Toast.LENGTH_LONG)
            endMeasurement()
            return
        }
        println(mlsRequest.toString())

        // save request params
        this.currentMeasurement?.mlsRequestParams = mlsRequest

        mlsRequest.considerIp = true
        mlsRequest.fallbacks.ipf = true
        mlsRequest.fallbacks.lacf = true

        // TODO secure API key
        Log.d(LOG_TAG, "Starting MLS geolocation request")
        mlsLocationService.geolocate(mlsRequest, "b4e52805e5534deb9d5cdb7df1000f36")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    run {
                        if (response.fallback == "ipf") {
                            showToast("WIFI and Cell Towers not known, fallback to GeoIP", Toast.LENGTH_LONG)
                        } else if (response.fallback == "lacf") {
                            showToast("WIFI and Cell Towers not known, fallback to LAC-based lookup", Toast.LENGTH_LONG)
                        }
                        // save response
                        Log.i(LOG_TAG, "MLS Request done")
                        this.currentMeasurement?.mlsResponse = response
                        this.checkMeasurementCompleted()
                    }
                }, { err ->
                    run {
                        val msg = "Querying MLS geolocation service resulted in an error"
                        showToast(msg + ": " + err.message, Toast.LENGTH_LONG)
                        Log.e(LOG_TAG, msg, err)
                        this.endMeasurement()
                    }
                })
    }


    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ManagerService.ManagerServiceBinder
            managerService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

}
