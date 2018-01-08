package at.ac.tuwien.mns.mnsgeolocation.fragments

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import at.ac.tuwien.mns.mnsgeolocation.BuildConfig
import at.ac.tuwien.mns.mnsgeolocation.R
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.service.OwnFileProvider
import at.ac.tuwien.mns.mnsgeolocation.util.DistanceUtils
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.view.MenuInflater
import android.widget.Toolbar


class DetailsFragment : Fragment() {

    private val LOG_TAG = javaClass.canonicalName

    private var measurement: Measurement? = null
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            measurement = arguments.getParcelable(ARG_MEASUREMENT)
        }
    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_detail, container, false)

        val time = view.findViewById<TextView>(R.id.timeValue)
        val distance = view.findViewById<TextView>(R.id.distanceValue)
        val mlsPos = view.findViewById<TextView>(R.id.mlsPosValue)
        val cells = view.findViewById<TextView>(R.id.cellValue)
        val wifi = view.findViewById<TextView>(R.id.wifiValue)
        val gpsPos = view.findViewById<TextView>(R.id.gpsPosValue)
        val accuracy = view.findViewById<TextView>(R.id.accuracyValue)

        time.text = dateFormat.format(measurement?.timestamp)
        val glat = measurement!!.gpsLocation!!.lat
        val glon = measurement!!.gpsLocation!!.lng
        val mlat = measurement!!.mlsResponse!!.location!!.lat!!
        val mlon = measurement!!.mlsResponse!!.location!!.lng!!
        val dist = DistanceUtils.haversineDistance(glat, glon, mlat, mlon)
        val decimalForm = DecimalFormat("#.##")
        distance.text = StringBuilder().append(decimalForm.format(dist)).append("m")
        mlsPos.text = StringBuilder().append(mlat).append("°N\n").append(mlon).append("°E")
        var b = StringBuilder()
        if (measurement?.mlsRequestParams!!.cellTowers.isNotEmpty()) {
            for (tower in measurement?.mlsRequestParams!!.cellTowers) {
                b.append("CID: ").append(tower.cellId).append(" Strength: ").append(tower.signalStrength).append("\n")
            }
            b.delete(b.length - 1, b.length)
        } else {
            b.append("No cell tower in range.")
        }
        cells.text = b
        b = StringBuilder()
        if (measurement?.mlsRequestParams!!.wifiAccessPoints.isNotEmpty()) {
            for (ap in measurement?.mlsRequestParams!!.wifiAccessPoints) {
                b.append("MAC: ").append(ap.macAddress).append(" Strength: ").append(ap.signalStrength).append("\n")
            }
            b.delete(b.length - 1, b.length)
        } else {
            b.append("No WiFi access point in range.")
        }
        wifi.text = b
        gpsPos.text = StringBuilder().append(glat).append("°N\n").append(glon).append("°E")
        accuracy.text = StringBuilder().append(decimalForm.format(measurement?.gpsLocation?.accuracy)).append("m")
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_details, menu)
        menu.findItem(R.id.action_delete).icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        menu.findItem(R.id.action_mail).icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                Toast.makeText(activity, "Delete selected", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_mail -> {
                // ----- email sending ------
                val m = measurement!!
                val cal = Calendar.getInstance()
                cal.timeInMillis = m.timestamp
                val localDate = SimpleDateFormat.getDateTimeInstance().format(cal.time)

                val filename = "measurement_" + SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(cal.time) + ".txt"
                val outputDir = activity.applicationContext.cacheDir // context being the Activity pointer
                val outputFile = File(outputDir, filename)
                if (!outputFile.exists()) {
                    try {
                        outputFile.createNewFile()
                    } catch (e: IOException) {
                        Log.e(LOG_TAG, "Could not create temp file for email attachment.")
                    }
                }

                val glat = m.gpsLocation!!.lat
                val glon = m.gpsLocation!!.lng
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
                emailIntent.putExtra(Intent.EXTRA_STREAM, OwnFileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID, outputFile))
                // give temporary permission to the folder containing the email attachment
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(emailIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        val ARG_MEASUREMENT = "measurement"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        fun newInstance(measurement: Measurement): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_MEASUREMENT, measurement)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
