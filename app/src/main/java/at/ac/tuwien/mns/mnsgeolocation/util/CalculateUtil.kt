package at.ac.tuwien.mns.mnsgeolocation.util

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Marton Bartal.
 */
class CalculateUtil {
    companion object {
        private val channelsFrequency = ArrayList<Int>(
                Arrays.asList(-1, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447,
                        2452, 2457, 2462, 2467, 2472, 2484))

        fun calculateAge(timeStamp: Long, timeUnit: TimeUnit): Int? {
            // something is wrong with either the timestamps or this calculation, maybe fix it? until then disabled
            return null
//            return (System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(timeStamp, timeUnit)).toInt()
        }

        // TODO 5 GhZ channels
        fun getChannelFromFrequency(frequency: Int): Int {
            return channelsFrequency.indexOf(Integer.valueOf(frequency))
        }
    }
}