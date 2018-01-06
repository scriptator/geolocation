package at.ac.tuwien.mns.mnsgeolocation.util;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by johannesvass on 06.01.18.
 */
public class CryptographyUtil {

    public static String encryptString(String plaintext, PublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        cipherOutputStream.write(plaintext.getBytes("UTF-8"));
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return Base64.encodeToString(vals, Base64.DEFAULT);
    }

    public static String decryptString(String ciphertext, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        cipherOutputStream.write(Base64.decode(ciphertext, Base64.DEFAULT));
        cipherOutputStream.close();

        return outputStream.toString("UTF-8");
    }
}
