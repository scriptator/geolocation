package at.ac.tuwien.mns.mnsgeolocation.service

import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams
import at.ac.tuwien.mns.mnsgeolocation.dto.LatLng
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface MLSLocationService {

    @POST("geolocate")
    fun geolocate(@Body params: GeolocationRequestParams,
                  @Query("key") key: String): Observable<GeolocationResponse>

    class GeolocationResponse {
        var location: LatLng? = null
        var accuracy: Float = Float.MAX_VALUE
        var fallback: String? = null
    }
}
