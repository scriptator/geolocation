package at.ac.tuwien.mns.mnsgeolocation.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Parcelable
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.util.PermissionUtil

/**
 * Created by Marton Bartal.
 */
class ManagerService : Service() {

    inner class ManagerServiceBinder : Binder() {
        fun getService(): ManagerService {
            return this@ManagerService
        }
    }

    companion object {
        val NOTIFICATION: String = "at.ac.tuwien.mns.mnsgeolocation.service.managerservice"
        val NOTIFICATION_TYPE: String = "notification_type"
        val NOTIFICATION_TYPE_PERM_FAILED: String = "nt_permission_failed"
        val NOTIFICATION_TYPE_MLS_REQUEST: String = "nt_mls_request"
        val NOTIFICATION_TYPE_LOCATION: String = "nt_location"
        val CONTENT: String = "content"
    }

    private val binder = ManagerServiceBinder()

    private val serviceStarter = Runnable {
        if (!PermissionUtil.locationPermissionGranted(this@ManagerService)
                || !PermissionUtil.wifiPermissionGranted(this@ManagerService)) {
            publishResults(NOTIFICATION_TYPE_PERM_FAILED, null)
        } else {
            startGPSIntent()
            startMLSScannerIntent()
        }
    }

    private val receiverGPS = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val location = intent?.extras?.getParcelable<Location>(GPSLocationService.LOCATION)
            println(location.toString())
            publishResults(NOTIFICATION_TYPE_LOCATION, location)
        }
    }

    private val receiverMLS = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val mlsRequest = intent?.extras?.getParcelable<GeolocationRequestParams>(MLSScannerService.MLS_REQUEST)
            println(mlsRequest.toString())
            publishResults(NOTIFICATION_TYPE_MLS_REQUEST, mlsRequest)
        }
    }

    override fun onCreate() {
        registerReceiver(receiverGPS, IntentFilter(GPSLocationService.NOTIFICATION))
        registerReceiver(receiverMLS, IntentFilter(MLSScannerService.NOTIFICATION))
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tryStartingServices()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(receiverGPS)
        unregisterReceiver(receiverMLS)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun tryStartingServices() {
        Handler().post(serviceStarter)
    }

    private fun publishResults(notifType: String, content: Parcelable?) {
        val publishIntent = Intent(NOTIFICATION)
        publishIntent.putExtra(NOTIFICATION_TYPE, notifType)
        publishIntent.putExtra(CONTENT, content)
        sendBroadcast(publishIntent)
    }

    private fun startGPSIntent() {
        val gpsIntent = Intent(this, GPSLocationService::class.java)
        startService(gpsIntent)
    }

    private fun startMLSScannerIntent() {
        val mlsScannerIntent = Intent(this, MLSScannerService::class.java)
        startService(mlsScannerIntent)
    }
}