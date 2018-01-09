package at.ac.tuwien.mns.mnsgeolocation.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.net.wifi.ScanResult
import android.os.Parcelable
import android.telephony.*
import at.ac.tuwien.mns.mnsgeolocation.Application
import at.ac.tuwien.mns.mnsgeolocation.dto.CellTower
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.dto.WifiAccessPoint
import at.ac.tuwien.mns.mnsgeolocation.util.CalculateUtil
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil


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
        val WIFI_DISABLED: String = "Wifi is disabled!"
        val NO_WIFI_NO_TOWERS_FOUND: String = "No wifi access points and no cell towers found!"
        val MLS_REQUEST: String = "mls_request"
    }

    override fun onHandleIntent(p0: Intent?) {
        val wifiAccessPoints = scanForWifiAccessPoints()
        if (wifiAccessPoints == null) {
            publishErr(WIFI_DISABLED)
            return
        }
        val cellTowers = scanForCellTowers()
        if (cellTowers.isEmpty() && wifiAccessPoints.isEmpty()) {
            publishErr(NO_WIFI_NO_TOWERS_FOUND)
        } else {
            val request = GeolocationRequestParams()
            request.wifiAccessPoints = wifiAccessPoints
            request.cellTowers = cellTowers
            publishResults(request)
        }
    }


    @SuppressLint("MissingPermission")
    private fun scanForCellTowers(): List<CellTower> {
        val telephonyManager = (application as Application).managerUtil.getTelephonyManager()
        val locationGranted = PermissionUtil.locationPermissionGranted(this)
        if (locationGranted) {
            return processCellInfos(telephonyManager.allCellInfo)
        }
        return emptyList()
    }

    @SuppressLint("MissingPermission")
    private fun scanForWifiAccessPoints(): List<WifiAccessPoint>? {
        val wifiManager = (application as Application).managerUtil.getWifiManager()
        val wifiGranted = PermissionUtil.wifiPermissionGranted(this)
        if (!PermissionUtil.wifiEnabled(this)) {
            return null
        }
        if (wifiGranted) {
            return processScanResults(wifiManager.scanResults)
        }
        return null
    }

    private fun publishErr(err: String?) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(TYPE, TYPE_ERR)
        publishIntent.putExtra(ERR_MSG, err)
        sendBroadcast(publishIntent)
    }

    private fun publishResults(geolocationRequestParams: GeolocationRequestParams) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(MLS_REQUEST, geolocationRequestParams as Parcelable)
        publishIntent.putExtra(TYPE, TYPE_SUCCESS)
        sendBroadcast(publishIntent)
    }

    private fun processScanResults(list: List<ScanResult>): List<WifiAccessPoint> {
        return list.filter { item ->
            !item.SSID.endsWith("_nomap")
        }.map { item ->
            WifiAccessPoint(item.BSSID, CalculateUtil.getChannelFromFrequency(item.frequency), item.frequency, item.level, null, null)
        }
    }

    private fun processCellInfos(list: List<CellInfo>): List<CellTower> {
        return list.map { item ->
            parseCellInfo(item)
        }.filter { item ->
            !item.cellId.equals(Integer.MAX_VALUE)
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
        return CellTower(mcc, mnc, lac, cid, radio, cellSignalStrength?.dbm, null, psc, ta)
    }
}