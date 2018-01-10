package at.ac.tuwien.mns.mnsgeolocation;

import at.ac.tuwien.mns.mnsgeolocation.dto.DaoSession;
import at.ac.tuwien.mns.mnsgeolocation.service.ServiceFactory;
import at.ac.tuwien.mns.mnsgeolocation.util.DbUtil;
import at.ac.tuwien.mns.mnsgeolocation.util.ManagerUtil;

/**
 * Created by johannesvass on 05.01.18.
 */
public class Application extends android.app.Application {

    private DbUtil dbUtil;
    private ManagerUtil managerUtil;
    private ServiceFactory serviceFactory;

    public Application() {
        dbUtil = new DbUtil();
        managerUtil = new ManagerUtil(this);
        serviceFactory = new ServiceFactory();
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public ManagerUtil getManagerUtil() {
        return managerUtil;
    }

    public void setManagerUtil(ManagerUtil managerUtil) {
        this.managerUtil = managerUtil;
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
