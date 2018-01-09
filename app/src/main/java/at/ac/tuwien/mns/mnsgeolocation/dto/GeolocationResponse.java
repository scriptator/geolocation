package at.ac.tuwien.mns.mnsgeolocation.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public final class GeolocationResponse implements Serializable, Parcelable {
    private Location location;
    private Float accuracy;
    private String fallback;

    public GeolocationResponse() {
    }

    private GeolocationResponse(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        if (in.readByte() == 0) {
            accuracy = null;
        } else {
            accuracy = in.readFloat();
        }
        fallback = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        if (accuracy == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(accuracy);
        }
        dest.writeString(fallback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GeolocationResponse> CREATOR = new Creator<GeolocationResponse>() {
        @Override
        public GeolocationResponse createFromParcel(Parcel in) {
            return new GeolocationResponse(in);
        }

        @Override
        public GeolocationResponse[] newArray(int size) {
            return new GeolocationResponse[size];
        }
    };

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
        return "GeolocationResponse{" + "location=" + location + ", accuracy=" + accuracy + ", " +
                "fallback='" + fallback + '\'' + '}';
    }
}
