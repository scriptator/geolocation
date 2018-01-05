package at.ac.tuwien.mns.mnsgeolocation.dto

import android.location.Location
import java.util.*

/**
 * Created by johannesvass on 03.01.18.
 */
class Measurement(var timestamp: Long,
                  var gpsLocation: Location?,
                  var mlsRequestParams: GeolocationRequestParams?,
                  var mlsResponse: GeolocationResponse?) {

    constructor() : this(Calendar.getInstance().timeInMillis,
            null,
            null,
            null) {
    }
}

