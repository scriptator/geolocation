package at.ac.tuwien.mns.mnsgeolocation.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Parcelable
import at.ac.tuwien.mns.mnsgeolocation.Application
import at.ac.tuwien.mns.mnsgeolocation.dto.Location
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil


/**
 * Created by Marton Bartal.
 */
class GPSLocationService : Service() {

    companion object {
        val NOTIFICATION: String = "at.ac.tuwien.mns.mnsgeolocation.service.gpslocation"
        val LOCATION: String = "locationResult"
        val TYPE: String = "result_type"
        val TYPE_ERR: String = "type_error"
        val ERR_MSG: String = "err_msg"
        val TYPE_SUCCESS: String = "type_success"
        val GPS_DISABLED: String = "GPS is disabled!"
    }

    @SuppressLint("MissingPermission")
    private val gpsStarter = Runnable {
        val locationManager = (application as Application).managerUtil.getLocationManager()
        val listener = GPSLocationListener()
        if (!PermissionUtil.gpsEnabled((application as Application))) {
            publishErr(GPS_DISABLED)
            return@Runnable
        }

        if (PermissionUtil.locationPermissionGranted(this@GPSLocationService)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, listener)
            publishResults(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        } else {
            println("Permission denied, something went wrong, permission was already checked.")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tryGPSStart()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun tryGPSStart() {
        Handler().post(gpsStarter)
    }

    // converts to own location class and sends intent
    private fun publishResults(location: android.location.Location?) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(LOCATION, Location(location) as Parcelable)
        publishIntent.putExtra(TYPE, TYPE_SUCCESS)
        sendBroadcast(publishIntent)
    }

    private fun publishErr(errMsg: String) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(TYPE, TYPE_ERR)
        publishIntent.putExtra(ERR_MSG, errMsg)
        sendBroadcast(publishIntent)
    }

    private inner class GPSLocationListener : LocationListener {

        override fun onLocationChanged(location: android.location.Location) {
            println("Location changed: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude())
            publishResults(location)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }
    }
}