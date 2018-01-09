package at.ac.tuwien.mns.mnsgeolocation;

import at.ac.tuwien.mns.mnsgeolocation.dto.DaoSession;
import at.ac.tuwien.mns.mnsgeolocation.util.DbUtil;

/**
 * Created by johannesvass on 05.01.18.
 */
public class Application extends android.app.Application {

    private DbUtil dbUtil;

    public Application() {
        dbUtil = new DbUtil();
    }

    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public DaoSession getDaoSession() {
        return dbUtil.getDaoSession();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbUtil.initDb(this);
    }
}
