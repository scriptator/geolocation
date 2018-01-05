package at.ac.tuwien.mns.mnsgeolocation

import android.content.Intent
import android.os.Bundle
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailsFragment
import android.view.MenuItem
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.service.OwnFileProvider
import at.ac.tuwien.mns.mnsgeolocation.util.DistanceUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by domin on 05.01.2018.
 */
class DetailsActivity : AppCompatActivity(), DetailsFragment.OnFragmentInteractionListener {

    private val LOG_TAG = javaClass.canonicalName

    private var measurement: Measurement? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        measurement = intent.extras.getParcelable(DetailsFragment.ARG_MEASUREMENT)
        val details = DetailsFragment.newInstance(measurement!!)
        fragmentManager.beginTransaction().replace(R.id.detail, details).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                Toast.makeText(this, "Delete selected", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_mail -> {
                // ----- email sending ------
                val m = measurement!!
                val cal = Calendar.getInstance()
                cal.timeInMillis = m.timestamp
                val localDate = SimpleDateFormat.getDateTimeInstance().format(cal.time)

                val filename = "measurement_" + SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(cal.time) + ".txt"
                val outputDir = applicationContext.cacheDir // context being the Activity pointer
                val outputFile = File(outputDir, filename)
                if (!outputFile.exists()) {
                    try {
                        outputFile.createNewFile()
                    } catch (e: IOException) {
                        Log.e(LOG_TAG, "Could not create temp file for email attachment.")
                    }
                }

                val glat = m.gpsLocation!!.latitude
                val glon = m.gpsLocation!!.longitude
                val mlat = m.mlsResponse!!.location!!.lat!!
                val mlon = m.mlsResponse!!.location!!.lng!!
                val distance = DistanceUtils.haversineDistance(glat, glon, mlat, mlon)

                val b = StringBuilder()
                b.append("GPS vs MLS measurement from ")
                        .append(localDate)
                        .append(":\n\n")
                b.append("GPS:\n")
                        .append("  Location: ")
                        .append(glat)
                        .append("°N / ")
                        .append(glon)
                        .append("°E\n")
                        .append("  Accuracy: ")
                        .append(m.gpsLocation?.accuracy)
                        .append(" m\n\n")
                b.append("MLS:\n")
                        .append("  Location: ")
                        .append(mlat)
                        .append("°N / ")
                        .append(mlon)
                        .append("°E\n")
                        .append("  Accuracy: ")
                        .append(m.mlsResponse?.accuracy)
                        .append(" m\n")
                        .append("  Parameters:\n")
                        .append("    Cell Towers:\n")
                for (tower in m.mlsRequestParams!!.cellTowers) {
                    b.append("      - ")
                    b.append(tower)
                    b.append("\n")
                }
                b.append("    WIFI Access Points:\n")
                for (ap in m.mlsRequestParams!!.wifiAccessPoints) {
                    b.append("      - ")
                    b.append(ap)
                    b.append("\n")
                }
                b.append("\nDistance: ")
                        .append(distance)
                        .append(" m")

                outputFile.writeText(b.toString())

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "text/plain"
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MNSMeasurement at " + localDate)
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello!\n\nThis email holds your measurement from " + localDate + " as an attachment.")
                // set the attachment, share via own file provider
                emailIntent.putExtra(Intent.EXTRA_STREAM, OwnFileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, outputFile))
                // give temporary permission to the folder containing the email attachment
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(emailIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}