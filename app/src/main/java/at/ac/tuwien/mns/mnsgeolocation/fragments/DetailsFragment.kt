package at.ac.tuwien.mns.mnsgeolocation.fragments

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.ac.tuwien.mns.mnsgeolocation.R
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.util.DistanceUtils
import org.w3c.dom.Text
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {

    private var measurement: Measurement? = null
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val glat = measurement!!.gpsLocation!!.latitude
        val glon = measurement!!.gpsLocation!!.longitude
        val mlat = measurement!!.mlsResponse!!.location!!.lat!!
        val mlon = measurement!!.mlsResponse!!.location!!.lng!!

        val dist = DistanceUtils.haversineDistance(glat, glon, mlat, mlon)

        time.text = dateFormat.format(measurement?.timestamp)
        distance.text = StringBuilder().append(dist).append("m")
        mlsPos.text = StringBuilder().append(mlat).append("°N\n").append(mlon).append("°E")
        var b = StringBuilder()
        for (tower in measurement?.mlsRequestParams!!.cellTowers) {
            b.append(tower.cellId).append("\n")
        }
        //b.delete(b.length-2,b.length)
        cells.text = b
        b = StringBuilder()
        for (ap in measurement?.mlsRequestParams!!.wifiAccessPoints) {
            b.append(ap.macAddress).append("\n")
        }
        //b.delete(b.length-2,b.length)
        wifi.text = b
        gpsPos.text = StringBuilder().append(glat).append("°N\n").append(glon).append("°E")
        accuracy.text = StringBuilder().append(measurement?.gpsLocation?.accuracy).append("m")
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
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
