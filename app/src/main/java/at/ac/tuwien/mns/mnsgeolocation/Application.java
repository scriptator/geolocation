package at.ac.tuwien.mns.mnsgeolocation;

import android.content.Context;

import at.ac.tuwien.mns.mnsgeolocation.dto.DaoMaster;
import at.ac.tuwien.mns.mnsgeolocation.dto.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by johannesvass on 05.01.18.
 */
public class Application extends android.app.Application {

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "measurements.db");
        // TODO secure password
        Database db = helper.getEncryptedWritableDb("1234");
        daoSession = new DaoMaster(db).newSession();
    }
}
