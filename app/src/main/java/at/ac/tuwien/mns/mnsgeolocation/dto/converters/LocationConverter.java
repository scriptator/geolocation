package at.ac.tuwien.mns.mnsgeolocation.dto.converters;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;

import at.ac.tuwien.mns.mnsgeolocation.dto.Location;

/**
 * Created by johannesvass on 05.01.18.
 */

public class LocationConverter implements PropertyConverter<Location, byte[]> {

    @Override
    public Location convertToEntityProperty(byte[] databaseValue) {
        Object deserialized = ByteArrayConverter.deserialize(databaseValue);
        if (deserialized instanceof Location) {
            return (Location) deserialized;
        } else {
            Log.e("LocationConverter", "Wrong type " + deserialized.getClass());
            return null;
        }
    }

    @Override
    public byte[] convertToDatabaseValue(Location entityProperty) {
        return ByteArrayConverter.serialize(entityProperty);
    }
}
