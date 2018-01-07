package at.ac.tuwien.mns.mnsgeolocation.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.telephony.*
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
        val TYPE: String = "result_type"
        val TYPE_ERR: String = "type_error"
        val ERR_MSG: String = "err_msg"
        val TYPE_SUCCESS: String = "type_success"
        val NO_WIFI_ACCESS_POINTS_FOUND: String = "No access points were found or Wifi is disabled!"
        val NO_TOWERS_FOUND: String = "No cell towers were found!"
        val MLS_REQUEST: String = "mls_request"
    }

    override fun onHandleIntent(p0: Intent?) {
        val wifiAccessPoints = scanForWifiAccessPoints()
        if (wifiAccessPoints.isEmpty()) {
            publishErr(NO_WIFI_ACCESS_POINTS_FOUND)
            return
        }
        val cellTowers = scanForCellTowers()
        if (cellTowers.isEmpty()) {
            publishErr(NO_TOWERS_FOUND)
        } else {
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
            return emptyList()
        }
        if (wifiGranted) {
            return processScanResults(wifiManager.scanResults)
        } else {
            println("Permission denied, something went wrong, permission was already checked.")
        }
        return emptyList()
    }

    private fun publishErr(err: String?) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(TYPE, TYPE_ERR)
        publishIntent.putExtra(ERR_MSG, err)
        sendBroadcast(publishIntent)
    }

    private fun publishResults(geolocationRequestParams: GeolocationRequestParams) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(MLS_REQUEST, geolocationRequestParams)
        publishIntent.putExtra(TYPE, TYPE_SUCCESS)
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