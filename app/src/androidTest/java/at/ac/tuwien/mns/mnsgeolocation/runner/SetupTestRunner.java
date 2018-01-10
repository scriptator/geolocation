package at.ac.tuwien.mns.mnsgeolocation.runner;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnitRunner;
import android.telephony.CellInfo;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;
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
import at.ac.tuwien.mns.mnsgeolocation.service.MLSLocationService;
import at.ac.tuwien.mns.mnsgeolocation.service.ServiceFactory;
import at.ac.tuwien.mns.mnsgeolocation.util.DbUtil;
import at.ac.tuwien.mns.mnsgeolocation.util.ManagerUtil;

import static org.mockito.Mockito.when;

/**
 * Created by Marton Bartal.
 */

public class SetupTestRunner extends AndroidJUnitRunner {

    public static final double LAT_1 = 48.210033;
    public static final double LON_1 = 16.363449;
    public static final double LAT_2 = 48.110033;
    public static final double LON_2 = 16.263449;
    public static final float ACCURACY = 64;
    public static final long DATE = 1515539026742L;
    public static final List<Measurement> MEASUREMENT_LIST;
    public static final int MEASUREMENT_LIST_SIZE = 20;
    private static final android.location.Location GPS_LOCATION;
    private static final List<CellInfo> MOCKED_CELL_INFO_LIST;
    private static final List<ScanResult> MOCKED_WIFI_SCAN_RESULT;

    static {
        GPS_LOCATION = createMockLocation(LAT_1, LON_1);

        List<Measurement> modifiableList = new ArrayList<>();
        for (Integer i = 0; i < MEASUREMENT_LIST_SIZE; i++) {
            modifiableList.add(new Measurement(i.longValue(), DATE + i * 60000, new Location
                    (createMockLocation(LAT_1 + i * 0.005, LON_1 + i * 0.005)),
                    createMockMLSRequest(), createMockResponse(LAT_2 + i * 0.005, LON_2 + i *
                    0.005, ACCURACY)));
        }
        MEASUREMENT_LIST = Collections.unmodifiableList(modifiableList);

        List<CellInfo> modifiableCIList = new ArrayList<>();
        // todo somehow create cellinfowcdma objects? reflection? magic?
        MOCKED_CELL_INFO_LIST = Collections.unmodifiableList(modifiableCIList);

        List<ScanResult> modifiableSCList = new ArrayList<>();
        // todo somehow create scanresult objects? reflection? magic?
        MOCKED_WIFI_SCAN_RESULT = Collections.unmodifiableList(modifiableSCList);
    }

    @Mock
    private DaoSession daoSession;
    @Mock
    private MeasurementDao measurementDao;
    @Mock
    private QueryBuilder<Measurement> measurementQueryBuilder;
    @Mock
    private Query<Measurement> measurementQuery;
    @Mock
    private TelephonyManager telephonyManager;
    @Mock
    private WifiManager wifiManager;
    @Mock
    private LocationManager locationManager;
    @Mock
    private MLSLocationService mlsLocationService;

    @Override
    public void callApplicationOnCreate(Application app) {
        MockitoAnnotations.initMocks(this);

        when(telephonyManager.getAllCellInfo()).thenReturn(MOCKED_CELL_INFO_LIST);
        when(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn
                (GPS_LOCATION);
        when(wifiManager.isWifiEnabled()).thenReturn(true);
        when(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
        when(wifiManager.getScanResults()).thenReturn(MOCKED_WIFI_SCAN_RESULT);

        when(daoSession.getMeasurementDao()).thenReturn(measurementDao);
        when(measurementDao.queryBuilder()).thenReturn(measurementQueryBuilder);
        when(measurementQueryBuilder.orderAsc(MeasurementDao.Properties.Id)).thenReturn
                (measurementQueryBuilder);
        when(measurementQueryBuilder.build()).thenReturn(measurementQuery);
        when(measurementQuery.list()).thenReturn(MEASUREMENT_LIST);

        if (app instanceof at.ac.tuwien.mns.mnsgeolocation.Application) {
            at.ac.tuwien.mns.mnsgeolocation.Application mApp = (at.ac.tuwien.mns.mnsgeolocation
                    .Application) app;
            mApp.setDbUtil(new DbUtil() {
                @Override
                public DaoSession getDaoSession() {
                    return daoSession;
                }

                @Override
                public void initDb(Application context) {
                }
            });
            mApp.setManagerUtil(new ManagerUtil(app) {
                @NotNull
                @Override
                public TelephonyManager getTelephonyManager() {
                    return telephonyManager;
                }

                @NotNull
                @Override
                public WifiManager getWifiManager() {
                    return wifiManager;
                }

                @NotNull
                @Override
                public LocationManager getLocationManager() {
                    return locationManager;
                }
            });
            mApp.setServiceFactory(new ServiceFactory() {
                @NotNull
                @Override
                public MLSLocationService getMlsLocationService() {
                    return mlsLocationService;
                }
            });
        }
        super.callApplicationOnCreate(app);
    }

    private static android.location.Location createMockLocation(double lat, double lon) {
        android.location.Location location = new android.location.Location(LocationManager
                .GPS_PROVIDER);
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAccuracy(ACCURACY);
        return location;
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

    private static GeolocationResponse createMockResponse(double lat, double lon, float accuracy) {
        GeolocationResponse response = new GeolocationResponse();
        response.setLocation(new Location(createMockLocation(lat, lon)));
        response.setAccuracy(accuracy);
        return response;
    }
}