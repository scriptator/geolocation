package at.ac.tuwien.mns.mnsgeolocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Toast;

import at.ac.tuwien.mns.mnsgeolocation.dto.DaoMaster;
import at.ac.tuwien.mns.mnsgeolocation.dto.DaoSession;
import at.ac.tuwien.mns.mnsgeolocation.util.CryptographyUtil;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.UUID;

import javax.crypto.NoSuchPaddingException;

/**
 * Created by johannesvass on 05.01.18.
 */
public class Application extends android.app.Application {

    private SharedPreferences prefs;
    private DaoSession daoSession;
    private KeyStore keyStore;

    private static final String APPLICATION_KEY_ALIAS = "application.key";
    private static final String DATABASE_ENCRYPTION_KEY_ALIAS = "measurements.db.key";
    private static final String LOG_TAG = "Application.main";

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = this.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

        String dbPassword;
        try {
            initializeKeyStore();
            dbPassword = getDbPassword();

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "measurements.db");
            Database db = helper.getEncryptedWritableDb(dbPassword);

            daoSession = new DaoMaster(db).newSession();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Something went wrong during the database initialization.",
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG,"Something went wrong during the crypto setup", e);
        }
    }

    private void initializeKeyStore() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, IOException, CertificateException, NoSuchPaddingException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        // create database key if not exists
        if (!keyStore.isKeyEntry(APPLICATION_KEY_ALIAS)) {
            Log.i(LOG_TAG, "Database key does not exist, creating entry in keystore.");
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    "AndroidKeyStore");
            AlgorithmParameterSpec spec;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                spec = new KeyGenParameterSpec.Builder(
                        APPLICATION_KEY_ALIAS,
                        KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build();
            } else {
                // TODO verify that it works
                spec = new KeyPairGeneratorSpec.Builder(this)
                        .setAlias(APPLICATION_KEY_ALIAS)
                        .build();
            }
            kpg.initialize(spec);
            kpg.generateKeyPair();
        }
    }

    public String getDbPassword() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, IOException {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(APPLICATION_KEY_ALIAS, null);
        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

        String encryptedPassword;
        if (prefs.contains(DATABASE_ENCRYPTION_KEY_ALIAS)) {
            encryptedPassword = prefs.getString(DATABASE_ENCRYPTION_KEY_ALIAS, "WON'T HAPPEN");
            Log.i(LOG_TAG, "Loaded encryped database password from shared prefs.");
        } else {
            encryptedPassword = CryptographyUtil.encryptString(UUID.randomUUID().toString(), publicKey);
            prefs.edit().putString(DATABASE_ENCRYPTION_KEY_ALIAS, encryptedPassword).apply();
            Log.i(LOG_TAG, "Stored new encryped database password in shared prefs.");
        }

        Log.i(LOG_TAG, "Decrypting password");
        return CryptographyUtil.decryptString(encryptedPassword, privateKeyEntry.getPrivateKey());
    }
}
