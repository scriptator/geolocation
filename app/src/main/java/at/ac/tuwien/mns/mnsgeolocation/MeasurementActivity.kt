package at.ac.tuwien.mns.mnsgeolocation

import android.app.Fragment
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailsFragment
import at.ac.tuwien.mns.mnsgeolocation.fragments.MeasurementsListFragment
import kotlinx.android.synthetic.main.activity_measurement_list.*


class MeasurementActivity : AppCompatActivity(), MeasurementsListFragment.OnMeasurementSelectedListener, DetailsFragment.OnMeasurementDeletedListener{

    private val LIST_TAG : String = "MEASUREMENT_LIST"
    private val DETAILS_TAG : String = "MEASUREMENT_DETAIL"

    private var measurementListFragment: MeasurementsListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_list)
        setSupportActionBar(toolbar)
        measurementListFragment = MeasurementsListFragment()
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.fragmentContainer, measurementListFragment, LIST_TAG).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backToList()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onMeasurementSelected(measurement: Measurement) {
        val details = DetailsFragment.newInstance(measurement)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            details.enterTransition = Slide(Gravity.RIGHT)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, details, DETAILS_TAG).addToBackStack(null).commit()
    }

    override fun onMeasurementDeleted(measurement: Measurement?) {
        if (measurementListFragment?.removeItem(measurement) == true)
            backToList()
    }

    private fun backToList() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        onBackPressed()
    }

}
