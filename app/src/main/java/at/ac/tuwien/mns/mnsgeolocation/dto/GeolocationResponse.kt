package at.ac.tuwien.mns.mnsgeolocation.dto

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by johannesvass on 03.01.18.
 */
class GeolocationResponse() : Parcelable {
    var location: LatLng? = null
    var accuracy: Float? = null
    var fallback: String? = null

    constructor(parcel: Parcel) : this() {
        accuracy = parcel.readValue(Float::class.java.classLoader) as? Float
        fallback = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accuracy)
        parcel.writeString(fallback)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GeolocationResponse> {
        override fun createFromParcel(parcel: Parcel): GeolocationResponse {
            return GeolocationResponse(parcel)
        }

        override fun newArray(size: Int): Array<GeolocationResponse?> {
            return arrayOfNulls(size)
        }
    }
}