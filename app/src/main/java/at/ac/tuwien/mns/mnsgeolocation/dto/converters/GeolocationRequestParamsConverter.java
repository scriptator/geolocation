package at.ac.tuwien.mns.mnsgeolocation.dto.converters;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;

import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams;

/**
 * Created by johannesvass on 05.01.18.
 */
public class GeolocationRequestParamsConverter implements PropertyConverter<GeolocationRequestParams, byte[]> {

    @Override
    public GeolocationRequestParams convertToEntityProperty(byte[] databaseValue) {
        Object deserialized = ByteArrayConverter.deserialize(databaseValue);
        if (deserialized instanceof GeolocationRequestParams) {
            return (GeolocationRequestParams) deserialized;
        } else {
            Log.e("GeolocationParamsConv.", "Wrong type " + deserialized.getClass());
            return null;
        }
    }

    @Override
    public byte[] convertToDatabaseValue(GeolocationRequestParams entityProperty) {
        return ByteArrayConverter.serialize(entityProperty);
    }
}
