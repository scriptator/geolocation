package at.ac.tuwien.mns.mnsgeolocation.util

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker

/**
 * Created by Marton Bartal.
 */
class PermissionUtil {
    companion object {
        fun locationPermissionGranted(context: Context): Boolean {
            return persmissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        fun wifiPermissionGranted(context: Context): Boolean {
            return persmissionGranted(context, Manifest.permission.ACCESS_WIFI_STATE)
        }

        private fun persmissionGranted(context: Context, permission: String): Boolean {
            return PermissionChecker.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
        }

        fun gpsEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun wifiEnabled(context: Context): Boolean {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return wifiManager.isWifiEnabled
        }
    }
}