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
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.dto.Location
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil


/**
 * Created by Marton Bartal.
 */
class GPSLocationService : Service() {

    @SuppressLint("MissingPermission")
    private val gpsStarter = Runnable {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = GPSLocationListener()
        //todo
//        if (!PermissionUtil.gpsEnabled(this)) {
//            Toast.makeText(this, "Please activate your GPS!", Toast.LENGTH_SHORT).show()
//            return
//        }

        if (PermissionUtil.locationPermissionGranted(this@GPSLocationService)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, listener)
            publishResults(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        } else {
            println("Permission denied, something went wrong, permission was already checked.")
            Toast.makeText(this@GPSLocationService, "Location permission required!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val NOTIFICATION: String = "at.ac.tuwien.mns.mnsgeolocation.service.gpslocation"
        val LOCATION: String = "locationResult"

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

        // TODO remove next block, this is just for mocking if the last location is unknown
        if (location == null) {
            val mockLocation = Location()
            mockLocation.lat = 48.210033
            mockLocation.lng = 16.363449
            mockLocation.accuracy = 25f
            val publishIntent = Intent(NOTIFICATION)
            publishIntent.putExtra(LOCATION, mockLocation as Parcelable)
            sendBroadcast(publishIntent)
            return
        }

        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(LOCATION, Location(location) as Parcelable)
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