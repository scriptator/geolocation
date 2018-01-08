package at.ac.tuwien.mns.mnsgeolocation

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.view.Gravity
import android.view.MenuItem
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import at.ac.tuwien.mns.mnsgeolocation.fragments.DetailsFragment
import at.ac.tuwien.mns.mnsgeolocation.fragments.MeasurementsListFragment
import kotlinx.android.synthetic.main.activity_measurement_list.*


class MeasurementActivity : AppCompatActivity(), MeasurementsListFragment.OnMeasurementSelectedListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_list)
        setSupportActionBar(toolbar)
    }

    override fun onMeasurementSelected(measurement: Measurement) {
        val details = DetailsFragment.newInstance(measurement)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            details.enterTransition = Slide(Gravity.RIGHT)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentManager.beginTransaction().replace(R.id.fragment, details).addToBackStack(null).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
