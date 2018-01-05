package at.ac.tuwien.mns.mnsgeolocation.dto;

import android.location.Location;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by johannesvass on 03.01.18.
 */
@Entity
public class Measurement {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Long timestamp;

    @Transient
    private Location gpsLocation;
    @Transient
    private GeolocationRequestParams mlsRequestParams;
    @Transient
    private GeolocationResponse mlsResponse;

    public Measurement(Long id, Long timestamp, Location gpsLocation, GeolocationRequestParams mlsRequestParams, GeolocationResponse mlsResponse) {
        this.id = id;
        this.timestamp = timestamp;
        this.gpsLocation = gpsLocation;
        this.mlsRequestParams = mlsRequestParams;
        this.mlsResponse = mlsResponse;
    }

    public Measurement() {
        this(null, Calendar.getInstance().getTimeInMillis(),
                null,
                null,
                null);
    }

    @Generated(hash = 116883927)
    public Measurement(Long id, @NotNull Long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

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


