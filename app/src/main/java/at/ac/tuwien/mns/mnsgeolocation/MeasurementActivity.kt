package at.ac.tuwien.mns.mnsgeolocation

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.adapters.MeasurementListAdapter
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.dto.Location
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.dto.MeasurementDao
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailsFragment
import at.ac.tuwien.mns.mnsgeolocation.service.MLSLocationService
import at.ac.tuwien.mns.mnsgeolocation.service.ManagerService
import at.ac.tuwien.mns.mnsgeolocation.service.ServiceFactory
import at.ac.tuwien.mns.mnsgeolocation.util.DisplayUtil
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_measurement_list.*
import java.util.*
import java.util.concurrent.TimeUnit


class MeasurementActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private val LOG_TAG = javaClass.canonicalName

    private var measurementDao: MeasurementDao? = null

    private var measurmentTimeout: Disposable? = null
    private var measuring = false
    private var currentMeasurement: Measurement? = null

    private var lastGPSLocation: Location? = null

    private var managerServiceIntent: Intent? = null
    private var managerService: ManagerService? = null
    private var mBound = false

    var progressOverlay: View? = null

    private val listItems: ArrayList<Measurement> = ArrayList()
    private var listAdapter: MeasurementListAdapter? = null

    private var mlsLocationService: MLSLocationService = ServiceFactory.getMlsLocationService()

    private val receiverManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.extras?.getString(ManagerService.NOTIFICATION_TYPE)
            println("msg type: " + type.toString())
            when {
                ManagerService.NOTIFICATION_TYPE_LOCATION.equals(type) -> processGPSLocationMsg(intent?.extras?.getParcelable<Location>(ManagerService.CONTENT))
                ManagerService.NOTIFICATION_TYPE_MLS_REQUEST.equals(type) -> conductMLSLocationRequest(intent?.extras?.getParcelable<GeolocationRequestParams>(ManagerService.CONTENT))
                ManagerService.NOTIFICATION_TYPE_MLS_ERR.equals(type) || ManagerService.NOTIFICATION_TYPE_LOCATION_ERR.equals(type) -> {
                    showErr(intent)
                    endMeasurement()
                }
                ManagerService.NOTIFICATION_TYPE_PERM_FAILED.equals(type) -> PermissionUtil.requestRequiredPermissions(this@MeasurementActivity)
            }
        }

        private fun showErr(intent: Intent?) {
            val errMsg = intent?.extras?.getString(ManagerService.CONTENT)
            if (errMsg != null) {
                showToast(errMsg, Toast.LENGTH_LONG)
            } else {
                showToast("An error occurred.", Toast.LENGTH_LONG)
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
        this.listAdapter = MeasurementListAdapter(listView.context, listItems)
        listView.adapter = listAdapter
        listView.onItemClickListener = this

        startManagerService()

        // get the DAO
        val daoSession = (application as Application).getDaoSession();
        if (daoSession != null) {
            measurementDao = daoSession.measurementDao

            // query all notes, sorted a-z by their text
            val measurementsQuery = measurementDao!!.queryBuilder().orderAsc(MeasurementDao.Properties.Id).build();
            val measurements: List<Measurement> = measurementsQuery.list()
            listItems.addAll(measurements)
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiverManager, IntentFilter(ManagerService.NOTIFICATION))
        bindService(managerServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.requestPermissionsResultFailed(requestCode, permissions, grantResults)) {
            showToast("Location permission required!", Toast.LENGTH_LONG)
        } else {
            managerService?.tryStartingServices()
        }
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val intent = Intent(parent?.context, DetailsActivity::class.java)
        intent.putExtra(DetailsFragment.ARG_MEASUREMENT, parent?.getItemAtPosition(pos) as Measurement)
        startActivity(intent)
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

        this.fab.setImageResource(R.drawable.ic_close_black_24dp)
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

        this.fab.setImageResource(R.drawable.ic_add_black_24dp)
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

            // insert it into the database
            m.id = this.measurementDao?.insert(m)

            listItems.add(m)
            if (listAdapter != null) {
                listAdapter?.notifyDataSetChanged()
            }
            Log.i(LOG_TAG, "Measurement complete: " + currentMeasurement)
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
