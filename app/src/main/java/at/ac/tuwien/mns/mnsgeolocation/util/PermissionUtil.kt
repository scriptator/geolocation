package at.ac.tuwien.mns.mnsgeolocation.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker

/**
 * Created by Marton Bartal.
 */
class PermissionUtil {
    companion object {
        private val APP_PERMISSION_REQUEST_CODE = 1

        fun locationPermissionGranted(context: Context): Boolean {
            return permissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        fun wifiPermissionGranted(context: Context): Boolean {
            return permissionGranted(context, Manifest.permission.ACCESS_WIFI_STATE)
        }

        fun gpsEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun wifiEnabled(context: Context): Boolean {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return wifiManager.isWifiEnabled
        }

        fun requestRequiredPermissions(activity: Activity) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE),
                    APP_PERMISSION_REQUEST_CODE)
        }

        /**
         * @return permission check failed?
         */
        fun requestPermissionsResultFailed(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
            if (APP_PERMISSION_REQUEST_CODE == requestCode) {
                return grantResults.isEmpty()
                        || grantResults.size != 2
                        || PermissionChecker.PERMISSION_GRANTED != grantResults[0]
                        || PermissionChecker.PERMISSION_GRANTED != grantResults[1]
            }
            return false
        }

        private fun permissionGranted(context: Context, permission: String): Boolean {
            return PermissionChecker.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
        }
    }
}