package at.ac.tuwien.mns.mnsgeolocation

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


class MeasurementActivity : AppCompatActivity(), DetailFragment.OnFragmentInteractionListener {

    private val LOG_TAG = javaClass.canonicalName

    private var currentMeasurement: Measurement? = null

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

        this.fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        this.fab.setOnClickListener {this.abortMeasurement()}

        // TODO is this ok like that?
        this.managerService?.tryStartingServices()
    }

    private fun abortMeasurement() {
        Log.i(LOG_TAG, "Aborting measurement")
        DisplayUtil.alphaAnimation(progressOverlay, View.GONE, 0f, 150)

        this.fab.setImageResource(android.R.drawable.ic_input_add)
        this.fab.setOnClickListener {this.startMeasurement()}
    }

    private fun processGPSLocationMsg(location: Location?) {
        if (location == null) {
            showToast("Last location unknown", Toast.LENGTH_LONG)
        } else {
            Log.i(LOG_TAG, "GPS location check done")
            this.currentMeasurement?.gpsLocation = location
            this.checkMeasurementCompleted()
        }
    }

    private fun checkMeasurementCompleted() {
        if (currentMeasurement?.gpsLocation != null
                && currentMeasurement?.mlsRequestParams != null
                && currentMeasurement?.mlsResponse != null) {

            DisplayUtil.alphaAnimation(progressOverlay, View.GONE, 0f, 150)
            this.fab.setImageResource(android.R.drawable.ic_input_add)
            this.fab.setOnClickListener {this.startMeasurement()}

            // TODO refactor list to display measurements and not a string
            val msg = "Lat: " + currentMeasurement?.gpsLocation?.latitude + ", Lon: " + currentMeasurement?.gpsLocation?.longitude
            showToast(msg, Toast.LENGTH_LONG)
            listItems.add(msg)
            if (listAdapter != null) {
                listAdapter?.notifyDataSetChanged()
            }
            Log.i(LOG_TAG, "Measurement complete: " + currentMeasurement)
        }
    }

    private fun conductMLSLocationRequest(mlsRequest: GeolocationRequestParams?) {
        if (mlsRequest == null) {
            showToast("Scanning wifi or cell towers failed", Toast.LENGTH_LONG)
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
                .doOnError {
                    // TODO handle different types of error (no internet, location not found, internal server error)
                    err ->
                    Log.e(LOG_TAG, err.message, err)
                }
                .subscribe { response ->
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
