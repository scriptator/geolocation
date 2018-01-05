package at.ac.tuwien.mns.mnsgeolocation.dto

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by johannesvass on 03.01.18.
 */
class GeolocationRequestParams() : Parcelable {

    var cellTowers: List<CellTower> = emptyList()
    var wifiAccessPoints: List<WifiAccessPoint> = emptyList()

    var considerIp = false
    var fallbacks = FallbackOptions(false, false)   // all fallbacks disabled

    constructor(parcel: Parcel) : this() {
        cellTowers = parcel.createTypedArrayList(CellTower)
        wifiAccessPoints = parcel.createTypedArrayList(WifiAccessPoint)
        considerIp = parcel.readByte() != 0.toByte()
    }

    class FallbackOptions {
        var lacf: Boolean = false
        var ipf: Boolean = false

        constructor()
        constructor(lacf: Boolean, ipf: Boolean) {
            this.lacf = lacf
            this.ipf = ipf
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(cellTowers)
        parcel.writeTypedList(wifiAccessPoints)
        parcel.writeByte(if (considerIp) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GeolocationRequestParams(cellTowers=$cellTowers, wifiAccessPoints=$wifiAccessPoints, considerIp=$considerIp, fallbacks=$fallbacks)"
    }


    companion object CREATOR : Parcelable.Creator<GeolocationRequestParams> {
        override fun createFromParcel(parcel: Parcel): GeolocationRequestParams {
            return GeolocationRequestParams(parcel)
        }

        override fun newArray(size: Int): Array<GeolocationRequestParams?> {
            return arrayOfNulls(size)
        }
    }
}