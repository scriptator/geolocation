package at.ac.tuwien.mns.mnsgeolocation.runner;

import android.app.Application;
import android.location.LocationManager;
import android.support.test.runner.AndroidJUnitRunner;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.ac.tuwien.mns.mnsgeolocation.dto.CellTower;
import at.ac.tuwien.mns.mnsgeolocation.dto.DaoSession;
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationRequestParams;
import at.ac.tuwien.mns.mnsgeolocation.dto.GeolocationResponse;
import at.ac.tuwien.mns.mnsgeolocation.dto.Location;
import at.ac.tuwien.mns.mnsgeolocation.dto.Measurement;
import at.ac.tuwien.mns.mnsgeolocation.dto.MeasurementDao;
import at.ac.tuwien.mns.mnsgeolocation.dto.WifiAccessPoint;
import at.ac.tuwien.mns.mnsgeolocation.util.DbUtil;

import static org.mockito.Mockito.when;

/**
 * Created by Marton Bartal.
 */

public class SetupTestRunner extends AndroidJUnitRunner {

    private static final double LAT_1 = 48.210033;
    private static final double LON_1 = 16.363449;
    private static final double LAT_2 = 48.110033;
    private static final double LON_2 = 16.263449;
    private static final float ACCURACY = 64;
    private static final List<Measurement> MEASUREMENT_LIST;

    static {
        List<Measurement> modifiableList = new ArrayList<>();
        modifiableList.add(new Measurement(1L, System.currentTimeMillis(), createMockLocation
                (LAT_1, LON_1), createMockMLSRequest(), createMockResponse()));
        MEASUREMENT_LIST = Collections.unmodifiableList(modifiableList);
    }

    @Mock
    private DaoSession daoSession;
    @Mock
    private MeasurementDao measurementDao;
    @Mock
    private QueryBuilder<Measurement> measurementQueryBuilder;
    @Mock
    private Query<Measurement> measurementQuery;

    @Override
    public void callApplicationOnCreate(Application app) {
        MockitoAnnotations.initMocks(this);

        when(daoSession.getMeasurementDao()).thenReturn(measurementDao);
        when(measurementDao.queryBuilder()).thenReturn(measurementQueryBuilder);
        when(measurementQueryBuilder.orderAsc(MeasurementDao.Properties.Id)).thenReturn
                (measurementQueryBuilder);
        when(measurementQueryBuilder.build()).thenReturn(measurementQuery);
        when(measurementQuery.list()).thenReturn(MEASUREMENT_LIST);

        if (app instanceof at.ac.tuwien.mns.mnsgeolocation.Application)
            ((at.ac.tuwien.mns.mnsgeolocation.Application) app).setDbUtil(new DbUtil() {
                @Override
                public DaoSession getDaoSession() {
                    return daoSession;
                }

                @Override
                public void initDb(Application context) {
                }
            });
        super.callApplicationOnCreate(app);
    }

    private static Location createMockLocation(double lat, double lon) {
        android.location.Location location = new android.location.Location(LocationManager
                .GPS_PROVIDER);
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAccuracy(ACCURACY);
        return new Location(location);
    }

    private static GeolocationRequestParams createMockMLSRequest() {
        GeolocationRequestParams result = new GeolocationRequestParams();

        List<CellTower> cellTowers = new ArrayList<>();
        CellTower cellTower = new CellTower(232, 5, 2012, 6771323, "wcdma", -83, null, 334, null);
        cellTowers.add(cellTower);

        List<WifiAccessPoint> wifiAccessPoints = new ArrayList<>();
        WifiAccessPoint wifiAccessPoint = new WifiAccessPoint("24:1f:a0:e8:3c:48", 1, 2412, -78,
                null, null);
        wifiAccessPoints.add(wifiAccessPoint);

        result.setCellTowers(cellTowers);
        result.setWifiAccessPoints(wifiAccessPoints);
        return result;
    }

    private static GeolocationResponse createMockResponse() {
        GeolocationResponse response = new GeolocationResponse();
        response.setLocation(createMockLocation(LAT_2, LON_2));
        response.setAccuracy(ACCURACY);
        return response;
    }
}