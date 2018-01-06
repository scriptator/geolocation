package at.ac.tuwien.mns.mnsgeolocation.dto.converters;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by johannesvass on 05.01.18.
 */
public class ByteArrayConverter {

    public static Object deserialize(byte[] databaseValue) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(databaseValue);
        ObjectInput objectInput = null;
        Object res = null;
        try {
            objectInput = new ObjectInputStream(inputStream);
            res = objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInput != null) {
                    objectInput.close();
                }
            } catch (IOException ex) {
                // ignore
            }
        }
        return res;
    }

    public static byte[] serialize(Object entity) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput;
        byte[] res = null;
        try {
            objectOutput = new ObjectOutputStream(outputStream);
            objectOutput.writeObject(entity);
            objectOutput.flush();
            res = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                // ignore
            }
        }
        return res;
    }
}
