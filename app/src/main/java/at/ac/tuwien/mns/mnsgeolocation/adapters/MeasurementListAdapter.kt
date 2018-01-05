package at.ac.tuwien.mns.mnsgeolocation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import java.text.SimpleDateFormat

/**
 * Created by Dominik on 05.01.2018.
 */
class MeasurementListAdapter(context: Context, measurements: List<Measurement>) : BaseAdapter() {

    private val inflater: LayoutInflater
    private var measurements : List<Measurement>
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

    companion object {
        class Holder {
            var textView: TextView? = null
        }
    }

    init {
        this.measurements = measurements
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return measurements.size
    }

    override fun getItem(i: Int): Any {
        return measurements[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var holder: MeasurementListAdapter.Companion.Holder;
        if (convertView == null) {
            val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            holder = MeasurementListAdapter.Companion.Holder()
            holder.textView = view.findViewById(android.R.id.text1)
            holder.textView?.text = dateFormat.format(measurements[position].timestamp)
            view.tag = holder
            return view
        } else {
            holder = convertView.tag as MeasurementListAdapter.Companion.Holder
            holder.textView?.text = dateFormat.format(measurements[position].timestamp)
            return convertView
        }
    }
}