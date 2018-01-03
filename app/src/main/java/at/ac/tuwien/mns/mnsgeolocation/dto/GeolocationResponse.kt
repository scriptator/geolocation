package at.ac.tuwien.mns.mnsgeolocation.dto

/**
 * Created by johannesvass on 03.01.18.
 */
class GeolocationResponse {
    var location: LatLng? = null
    var accuracy: Float? = null
    var fallback: String? = null
}