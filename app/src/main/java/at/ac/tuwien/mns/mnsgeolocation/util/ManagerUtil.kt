package at.ac.tuwien.mns.mnsgeolocation.util

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager

/**
 * Created by Marton Bartal.
 */

open class ManagerUtil(private val context: Context) {

    open fun getTelephonyManager(): TelephonyManager {
        return context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    open fun getWifiManager(): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    open fun getLocationManager(): LocationManager {
        return context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
