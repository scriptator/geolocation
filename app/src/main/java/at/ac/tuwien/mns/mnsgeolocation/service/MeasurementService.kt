package at.ac.tuwien.mns.mnsgeolocation.service

import android.location.Location
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationResponse
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement
import java.util.*

/**
 *
 */
class MeasurementService {

    /**
     * TODO think of good signature for this function
     * on which thread should it run?
     * reactive or not?
     * ...
     */
    fun conductMeasurement(): Measurement {
        val currentTime: Long = Calendar.getInstance().getTimeInMillis()

        return Measurement(currentTime,
                Location("TODO"),
                GeolocationRequestParams(),
                GeolocationResponse())
    }
}