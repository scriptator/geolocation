package at.ac.tuwien.mns.mnsgeolocation.dto

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

/**
 * Created by johannesvass on 03.01.18.
 */
class Measurement(var timestamp: Long,
                  var gpsLocation: Location?,
                  var mlsRequestParams: GeolocationRequestParams?,
                  var mlsResponse: GeolocationResponse?): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readParcelable(GeolocationRequestParams::class.java.classLoader),
            parcel.readParcelable(GeolocationResponse::class.java.classLoader)) {
    }

    constructor() : this(Calendar.getInstance().timeInMillis,
            null,
            null,
            null) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(timestamp)
        parcel.writeParcelable(gpsLocation, flags)
        parcel.writeParcelable(mlsRequestParams, flags)
        parcel.writeParcelable(mlsResponse, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Measurement> {
        override fun createFromParcel(parcel: Parcel): Measurement {
            return Measurement(parcel)
        }

        override fun newArray(size: Int): Array<Measurement?> {
            return arrayOfNulls(size)
        }
    }
}

