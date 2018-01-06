package at.ac.tuwien.mns.mnsgeolocation.dto;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Calendar;
import org.greenrobot.greendao.annotation.Generated;

import at.ac.tuwien.mns.mnsgeolocation.dto.converters.GeolocationRequestParamsConverter;
import at.ac.tuwien.mns.mnsgeolocation.dto.converters.GeolocationResponseConverter;
import at.ac.tuwien.mns.mnsgeolocation.dto.converters.LocationConverter;

/**
 * Created by johannesvass on 03.01.18.
 */
@Entity
public class Measurement {

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


