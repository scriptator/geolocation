package at.ac.tuwien.mns.mnsgeolocation.dto

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
                      val age: Int? = null
                      ): Serializable {}
