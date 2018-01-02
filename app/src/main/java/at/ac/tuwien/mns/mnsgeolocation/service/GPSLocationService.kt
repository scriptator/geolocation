package at.ac.tuwien.mns.mnsgeolocation.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.widget.Toast


/**
 * Created by Marton Bartal.
 */
class GPSLocationService : IntentService("GPSLocationService") {

    companion object {
        val NOTIFICATION: String = "at.ac.tuwien.mns.mnsgeolocation.service.gpslocation"
        val LOCATION: String = "locationResult"

        fun locationPermissionGranted(context: Context): Boolean {
            return PermissionChecker.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        fun gpsEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onHandleIntent(p0: Intent?) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = GPSLocationListener()
        if (!Companion.gpsEnabled(this)) {
            Toast.makeText(this, "Please activate your GPS!", Toast.LENGTH_SHORT).show()
            return
        }

        if (locationPermissionGranted(this)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, listener)
            publishResults(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        } else {
            println("Permission denied, something went wrong, permission was already checked.")
            Toast.makeText(this, "Location permission required !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun publishResults(location: Location?) {

        // TODO remove next block, this is just for mocking if the last location is unknown
        if (location == null) {
            val mockLocation = Location(LocationManager.GPS_PROVIDER)
            mockLocation.latitude = 48.210033
            mockLocation.longitude = 16.363449
            val publishIntent = Intent(NOTIFICATION)
            publishIntent.putExtra(LOCATION, mockLocation)
            sendBroadcast(publishIntent)
            return
        }

        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(LOCATION, location)
        sendBroadcast(publishIntent)
    }

    private inner class GPSLocationListener : LocationListener {

        override fun onLocationChanged(location: Location) {
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