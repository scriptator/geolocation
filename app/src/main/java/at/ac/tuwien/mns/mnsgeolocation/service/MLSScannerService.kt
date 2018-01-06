package at.ac.tuwien.mns.mnsgeolocation.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.telephony.*
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.dto.CellTower
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.dto.WifiAccessPoint
import at.ac.tuwien.mns.mnsgeolocation.util.CalculateUtil
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil
import java.util.concurrent.TimeUnit


/**
 * Created by Marton Bartal.
 */
class MLSScannerService : IntentService("MLSScannerService") {

    companion object {
        val NOTIFICATION: String = "at.ac.tuwien.mns.mnsgeolocation.service.mlsscanner"
        val MLS_REQUEST: String = "mls_request"
    }

    override fun onHandleIntent(p0: Intent?) {
        val wifiAccessPoints = scanForWifiAccessPoints()
        // TODO why was this there? When there is no wifi in range it won't work
//        if (wifiAccessPoints.isEmpty()) {
//            return
//        }
        val cellTowers = scanForCellTowers()
        //if (wifiAccessPoints.isEmpty() && cellTowers.isEmpty()) {
            // FIXME this does not work (null not allowed) - what shall we do when nothing is available?
            // publishResults(null)
       //  } else {
            val request = GeolocationRequestParams()
            request.wifiAccessPoints = wifiAccessPoints
            request.cellTowers = cellTowers
            publishResults(request)
        //}
    }


    @SuppressLint("MissingPermission")
    private fun scanForCellTowers(): List<CellTower> {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val locationGranted = PermissionUtil.locationPermissionGranted(this)
        if (locationGranted) {
            return processCellInfos(telephonyManager.allCellInfo)
        }
        return emptyList()
    }

    @SuppressLint("MissingPermission")
    private fun scanForWifiAccessPoints(): List<WifiAccessPoint> {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiGranted = PermissionUtil.wifiPermissionGranted(this)
        if (!PermissionUtil.wifiEnabled(this)) {
            Toast.makeText(this, "Please activate your Wifi!", Toast.LENGTH_SHORT).show()
            return emptyList()
        }
        if (wifiGranted) {
            return processScanResults(wifiManager.scanResults)
        } else {
            println("Permission denied, something went wrong, permission was already checked.")
            Toast.makeText(this, "Wifi permission required!", Toast.LENGTH_SHORT).show()
        }
        return emptyList()
    }

    private fun publishResults(geolocationRequestParams: GeolocationRequestParams?) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(MLS_REQUEST, geolocationRequestParams as Parcelable)
        sendBroadcast(publishIntent)
    }

    private fun processScanResults(list: List<ScanResult>): List<WifiAccessPoint> {
        return list.filter { item ->
            !item.SSID.endsWith("_nomap")
        }.map { item ->
            val age = CalculateUtil.calculateAge(item.timestamp, TimeUnit.MICROSECONDS)
            WifiAccessPoint(item.BSSID, CalculateUtil.getChannelFromFrequency(item.frequency), item.frequency, item.level, null, age)
        }
    }

    private fun processCellInfos(list: List<CellInfo>): List<CellTower> {
        return list.map { item ->
            parseCellInfo(item)
        }
    }

    private fun parseCellInfo(cellInfo: CellInfo): CellTower {
        var mcc = 0
        var mnc = 0
        var lac = 0
        var cid = 0
        var radio = ""
        var cellSignalStrength: CellSignalStrength? = null
        var psc: Int? = null
        var ta: Int? = null

        if (cellInfo is CellInfoWcdma) {
            mcc = cellInfo.cellIdentity.mcc
            mnc = cellInfo.cellIdentity.mnc
            lac = cellInfo.cellIdentity.lac
            cid = cellInfo.cellIdentity.cid
            cellSignalStrength = cellInfo.cellSignalStrength
            psc = cellInfo.cellIdentity.psc
            radio = "wcdma"
        } else if (cellInfo is CellInfoLte) {
            mcc = cellInfo.cellIdentity.mcc
            mnc = cellInfo.cellIdentity.mnc
            lac = cellInfo.cellIdentity.tac
            cid = cellInfo.cellIdentity.ci
            cellSignalStrength = cellInfo.cellSignalStrength
            psc = cellInfo.cellIdentity.pci
            ta = cellInfo.cellSignalStrength.timingAdvance
            radio = "lte"
        } else if (cellInfo is CellInfoGsm) {
            mcc = cellInfo.cellIdentity.mcc
            mnc = cellInfo.cellIdentity.mnc
            lac = cellInfo.cellIdentity.lac
            cid = cellInfo.cellIdentity.cid
            cellSignalStrength = cellInfo.cellSignalStrength
            radio = "gsm"
        }
        val age = CalculateUtil.calculateAge(cellInfo.timeStamp, TimeUnit.NANOSECONDS)
        return CellTower(mcc, mnc, lac, cid, radio, cellSignalStrength?.dbm, age, psc, ta)
    }
}