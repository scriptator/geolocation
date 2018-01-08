package at.ac.tuwien.mns.mnsgeolocation.util

/**
 * Created by Marton Bartal.
 */
class CalculateUtil {
    companion object {
        private val channelsFrequencyMap = hashMapOf(2412 to 1,
                2417 to 2, 2422 to 3, 2427 to 4, 2432 to 5, 2437 to 6, 2442 to 7, 2447 to 8,
                2452 to 9, 2457 to 10, 2462 to 11, 2467 to 12, 2472 to 13, 2484 to 14, 5180 to 36,
                5200 to 40, 5220 to 44, 5240 to 48, 5260 to 52, 5280 to 56, 5300 to 60, 5320 to 64,
                5500 to 100, 5520 to 104, 5540 to 108, 5560 to 112, 5580 to 116, 5600 to 120,
                5620 to 124, 5640 to 128, 5660 to 132, 5680 to 136, 5700 to 140, 5745 to 149,
                5765 to 153, 5785 to 157, 5805 to 161, 5825 to 165)

        fun getChannelFromFrequency(frequency: Int): Int? {
            return channelsFrequencyMap.get(frequency)
        }
    }
}