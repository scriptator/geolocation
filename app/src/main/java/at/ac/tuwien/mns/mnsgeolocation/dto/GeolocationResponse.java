package at.ac.tuwien.mns.mnsgeolocation.dto;

import java.io.Serializable;

public final class GeolocationResponse implements Serializable {
    private Location location;
    private Float accuracy;
    private String fallback;

    public final Location getLocation() {
        return this.location;
    }

    public final void setLocation(Location var1) {
        this.location = var1;
    }

    public final Float getAccuracy() {
        return this.accuracy;
    }

    public final void setAccuracy(Float var1) {
        this.accuracy = var1;
    }

    public final String getFallback() {
        return this.fallback;
    }

    public final void setFallback(String var1) {
        this.fallback = var1;
    }

    @Override
    public String toString() {
        return "GeolocationResponse{" +
                "location=" + location +
                ", accuracy=" + accuracy +
                ", fallback='" + fallback + '\'' +
                '}';
    }
}
