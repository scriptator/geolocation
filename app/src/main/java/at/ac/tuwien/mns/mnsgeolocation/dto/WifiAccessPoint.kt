package at.ac.tuwien.mns.mnsgeolocation.dto

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Immutable wifi access point class
 *
 * macAddress is required and non-null
 * other fields default to null and will be not serialized by gson in this case
 */
class WifiAccessPoint(val macAddress: String,
                      val channel: Int? = null,
                      val frequency: Int? = null,
                      val signalStrength: Int? = null,
                      val signalToNoiseRatio: Int? = null,
                      val age: Int? = null) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(macAddress)
        parcel.writeValue(channel)
        parcel.writeValue(frequency)
        parcel.writeValue(signalStrength)
        parcel.writeValue(signalToNoiseRatio)
        parcel.writeValue(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "WifiAccessPoint(macAddress='$macAddress', channel=$channel, frequency=$frequency, signalStrength=$signalStrength, signalToNoiseRatio=$signalToNoiseRatio, age=$age)"
    }


    companion object CREATOR : Parcelable.Creator<WifiAccessPoint> {
        override fun createFromParcel(parcel: Parcel): WifiAccessPoint {
            return WifiAccessPoint(parcel)
        }

        override fun newArray(size: Int): Array<WifiAccessPoint?> {
            return arrayOfNulls(size)
        }
    }
}
