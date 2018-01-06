package at.ac.tuwien.mns.mnsgeolocation.dto.converters;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;

import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationResponse;

/**
 * Created by johannesvass on 05.01.18.
 */

public class GeolocationResponseConverter implements PropertyConverter<GeolocationResponse, byte[]> {

    @Override
    public GeolocationResponse convertToEntityProperty(byte[] databaseValue) {
        Object deserialized = ByteArrayConverter.deserialize(databaseValue);
        if (deserialized instanceof GeolocationResponse) {
            return (GeolocationResponse) deserialized;
        } else {
            Log.e("GeolocationParamsConv.", "Wrong type " + (deserialized != null ? deserialized.getClass() : null));
            return null;
        }
    }

    @Override
    public byte[] convertToDatabaseValue(GeolocationResponse entityProperty) {
        return ByteArrayConverter.serialize(entityProperty);
    }
}
