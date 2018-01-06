package at.ac.tuwien.mns.mnsgeolocation.dto

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Immutable cell tower class
 *
 * mcc, mnc, lac, cid and radioType are required and non-null
 * other fields default to null and will be not serialized by gson in this case
 */
open class CellTower(val mobileCountryCode: Int,
                     val mobileNetworkCode: Int,
                     val locationAreaCode: Int,
                     val cellId: Int,
                     val radioType: String,
                     val signalStrength: Int? = null,
                     val age: Int? = null,
                     val psc: Int? = null,
                     val timingAdvance: Int? = null) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mobileCountryCode)
        parcel.writeInt(mobileNetworkCode)
        parcel.writeInt(locationAreaCode)
        parcel.writeInt(cellId)
        parcel.writeString(radioType)
        parcel.writeValue(signalStrength)
        parcel.writeValue(age)
        parcel.writeValue(psc)
        parcel.writeValue(timingAdvance)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "CellTower(mobileCountryCode=$mobileCountryCode, mobileNetworkCode=$mobileNetworkCode, locationAreaCode=$locationAreaCode, cellId=$cellId, radioType='$radioType', signalStrength=$signalStrength, age=$age, psc=$psc, timingAdvance=$timingAdvance)"
    }

    companion object CREATOR : Parcelable.Creator<CellTower> {
        override fun createFromParcel(parcel: Parcel): CellTower {
            return CellTower(parcel)
        }

        override fun newArray(size: Int): Array<CellTower?> {
            return arrayOfNulls(size)
        }
    }
}
