package at.ac.tuwien.mns.mnsgeolocation.util;

public class DistanceUtils {

    // earth radius in metres
    public static int EARTH_RADIUS = 6371000;

    /**
     * Given two points in latitude/longitude this function calculates the shortest distance
     * on the earth surface.
     * <p>
     * see also https://en.wikipedia.org/wiki/Haversine_formula
     * <p>
     * The mean earth radius of 6371 km is used, because 'this value is recommended by the International Union of Geodesy
     * and Geophysics and it minimizes the RMS relative error between the great circle and geodesic distance.', as
     * Rosettacode tells us (http://rosettacode.org/wiki/Haversine_formula)
     *
     * @return the distance in meters
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = haversine(deltaPhi) + Math.cos(phi1) * Math.cos(phi2) * haversine(deltaLambda);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * https://en.wikipedia.org/wiki/Haversine_formula
     */
    public static double haversine(double phi) {
        return (1 - Math.cos(phi)) / 2;
    }
}
