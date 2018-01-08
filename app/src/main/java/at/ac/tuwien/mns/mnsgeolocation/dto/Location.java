package at.ac.tuwien.mns.mnsgeolocation.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by johannesvass on 03.01.18.
 */
public class Location implements Parcelable, Serializable {
    private Double lat;
    private Double lng;
    private Float accuracy;

    public Location() {
    }

    public Location(android.location.Location other) {
        this();
        if (other == null) {
            return;
        }

        this.lat = other.getLatitude();
        this.lng = other.getLongitude();
        this.accuracy = other.getAccuracy();
    }

    protected Location(Parcel in) {
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lng = null;
        } else {
            lng = in.readDouble();
        }
        if (in.readByte() == 0) {
            accuracy = null;
        } else {
            accuracy = in.readFloat();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lat);
        }
        if (lng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lng);
        }
        if (accuracy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(accuracy);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (lat != null ? !lat.equals(location.lat) : location.lat != null) return false;
        if (lng != null ? !lng.equals(location.lng) : location.lng != null) return false;
        return accuracy != null ? accuracy.equals(location.accuracy) : location.accuracy == null;
    }

    @Override
    public int hashCode() {
        int result = lat != null ? lat.hashCode() : 0;
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        result = 31 * result + (accuracy != null ? accuracy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", accuracy=" + accuracy +
                '}';
    }
}