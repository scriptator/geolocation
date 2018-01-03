package at.ac.tuwien.mns.mnsgeolocation.dto

import android.location.Location
import java.time.LocalDateTime

/**
 * Created by johannesvass on 03.01.18.
 */
class Measurement(val timestamp: Long,
                  val gpsLocation: Location,
                  val mlsRequestParams: GeolocationRequestParams,
                  val mlsResponse: GeolocationResponse) {}