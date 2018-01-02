package at.ac.tuwien.mns.mnsgeolocation

import android.Manifest
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailFragment
import at.ac.tuwien.mns.mnsgeolocation.service.GPSLocationService
import kotlinx.android.synthetic.main.activity_measurement_list.*

class MeasurementListActivity : AppCompatActivity(), DetailFragment.OnFragmentInteractionListener {

    private val listItems: ArrayList<String> = ArrayList()
    private var listAdapter: ArrayAdapter<String>? = null

    companion object {
        private val LOCATION_PERMISSION_CODE = 1
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                val location = bundle.getParcelable<Location>(GPSLocationService.LOCATION)
                if (location == null) {
                    showToast("Last location unknown", Toast.LENGTH_LONG)
                } else {
                    val msg = "Lat: " + location.latitude + ", Lon: " + location.longitude
                    showToast(msg, Toast.LENGTH_LONG)
                    listItems.add(msg)
                    if (listAdapter != null) {
                        listAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_measurement_list)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val listView = findViewById<ListView>(R.id.listView) as ListView
        this.listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, listItems)
        listView.adapter = listAdapter

        var startIntent = true
        if (!GPSLocationService.locationPermissionGranted(this)) {
            ActivityCompat.requestPermissions(this,
                    Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                    LOCATION_PERMISSION_CODE)
            startIntent = false // start on callback not here
        }
        if (!GPSLocationService.gpsEnabled(this)) {
            showToast("Please activate your GPS and restart the app !", Toast.LENGTH_LONG)
            startIntent = false
        }
        if (startIntent) {
            startGPSIntent()
        }
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
        if (LOCATION_PERMISSION_CODE == requestCode) {
            var failed = grantResults.isEmpty()
                    || grantResults.size != 1
                    || PermissionChecker.PERMISSION_GRANTED != grantResults[0]
            if (failed) {
                showToast("Location permission required!", Toast.LENGTH_LONG)
            } else {
                startGPSIntent()
            }
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        //TODO
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(GPSLocationService.NOTIFICATION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun showToast(text: String, duration: Int) {
        Toast.makeText(this, text, duration).show()
    }

    private fun startGPSIntent() {
        val gpsIntent = Intent(this, GPSLocationService::class.java)
        startService(gpsIntent)
    }
}
