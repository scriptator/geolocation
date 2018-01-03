package at.ac.tuwien.mns.mnsgeolocation.dto

/**
 * Created by johannesvass on 03.01.18.
 */
class GeolocationRequestParams {

    var cellTowers: List<CellTower> = emptyList()
    var wifiAccessPoints: List<WifiAccessPoint> = emptyList()

    var considerIp = false
    var fallbacks = FallbackOptions(false, false)   // all fallbacks disabled

    class FallbackOptions {
        var lacf: Boolean = false
        var ipf: Boolean = false

        constructor()
        constructor(lacf: Boolean, ipf: Boolean) {
            this.lacf = lacf
            this.ipf = ipf
        }
    }
}