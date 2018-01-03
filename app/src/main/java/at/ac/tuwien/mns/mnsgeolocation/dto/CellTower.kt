package at.ac.tuwien.mns.mnsgeolocation.dto

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
                     val timingAdvance: Int? = null) : Serializable {}
