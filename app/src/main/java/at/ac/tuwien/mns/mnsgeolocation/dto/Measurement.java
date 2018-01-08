package at.ac.tuwien.mns.mnsgeolocation.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Calendar;

import at.ac.tuwien.mns.mnsgeolocation.dto.converters.GeolocationRequestParamsConverter;
import at.ac.tuwien.mns.mnsgeolocation.dto.converters.GeolocationResponseConverter;
import at.ac.tuwien.mns.mnsgeolocation.dto.converters.LocationConverter;

/**
 * Created by johannesvass on 03.01.18.
 */
@Entity
public class Measurement implements Parcelable {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Long timestamp;

    @Convert(converter = LocationConverter.class, columnType = byte[].class)
    private Location gpsLocation;
    @Convert(converter = GeolocationRequestParamsConverter.class, columnType = byte[].class)
    private GeolocationRequestParams mlsRequestParams;
    @Convert(converter = GeolocationResponseConverter.class, columnType = byte[].class)
    private GeolocationResponse mlsResponse;

    @Generated(hash = 946977947)
    public Measurement(Long id, @NotNull Long timestamp, Location gpsLocation, GeolocationRequestParams mlsRequestParams,
            GeolocationResponse mlsResponse) {
        this.id = id;
        this.timestamp = timestamp;
        this.gpsLocation = gpsLocation;
        this.mlsRequestParams = mlsRequestParams;
        this.mlsResponse = mlsResponse;
    }

    @Keep
    public Measurement() {
        this(null, Calendar.getInstance().getTimeInMillis(),
                null,
                null,
                null);
    }

    protected Measurement(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
        gpsLocation = in.readParcelable(Location.class.getClassLoader());
        mlsRequestParams = in.readParcelable(GeolocationRequestParams.class.getClassLoader());
        mlsResponse = in.readParcelable(GeolocationResponse.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        if (timestamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timestamp);
        }
        dest.writeParcelable(gpsLocation, flags);
        dest.writeParcelable(mlsRequestParams, flags);
        dest.writeParcelable(mlsResponse, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Location getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(Location gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public GeolocationRequestParams getMlsRequestParams() {
        return mlsRequestParams;
    }

    public void setMlsRequestParams(GeolocationRequestParams mlsRequestParams) {
        this.mlsRequestParams = mlsRequestParams;
    }

    public GeolocationResponse getMlsResponse() {
        return mlsResponse;
    }

    public void setMlsResponse(GeolocationResponse mlsResponse) {
        this.mlsResponse = mlsResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (gpsLocation != null ? !gpsLocation.equals(that.gpsLocation) : that.gpsLocation != null)
            return false;
        if (mlsRequestParams != null ? !mlsRequestParams.equals(that.mlsRequestParams) : that.mlsRequestParams != null)
            return false;
        return mlsResponse != null ? mlsResponse.equals(that.mlsResponse) : that.mlsResponse == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (gpsLocation != null ? gpsLocation.hashCode() : 0);
        result = 31 * result + (mlsRequestParams != null ? mlsRequestParams.hashCode() : 0);
        result = 31 * result + (mlsResponse != null ? mlsResponse.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", gpsLocation=" + gpsLocation +
                ", mlsRequestParams=" + mlsRequestParams +
                ", mlsResponse=" + mlsResponse +
                '}';
    }
}


