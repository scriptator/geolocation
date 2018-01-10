package at.ac.tuwien.mns.mnsgeolocation

import android.Manifest
import android.annotation.SuppressLint
import android.support.test.runner.AndroidJUnit4
import at.ac.tuwien.mns.mnsgeolocation.runner.SetupTestRunner
import at.ac.tuwien.mns.mnsgeolocation.util.DistanceUtils
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.PermissionGranter
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import android.support.test.InstrumentationRegistry
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains
import com.schibsted.spain.barista.interaction.BaristaListInteractions.scrollListToPosition
import com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import java.util.Date


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GeolocationBaristaTest {

    @Rule
    @JvmField
    var baristaRule = BaristaRule.create(MeasurementActivity::class.java)

    @SuppressLint("SimpleDateFormat")
    private val DECIMAL_FORMAT = DecimalFormat("#.##")
    private val DATE_FORMAT = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    private val POSITION_LIST_ITEM = 2

    @Before
    fun setUp() {
        baristaRule.launchActivity()
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_WIFI_STATE)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.INTERNET)
    }

    @Test
    fun makeMeasurementTest() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH")
        val now = Date()
        val date = dateFormat.format(now)
        val distance = DECIMAL_FORMAT.format(DistanceUtils.haversineDistance(SetupTestRunner.LAT_1, SetupTestRunner.LON_1, SetupTestRunner.NEW_LAT, SetupTestRunner.NEW_LON))
        val accuracy = DECIMAL_FORMAT.format(SetupTestRunner.ACCURACY)

        clickOn(R.id.fab)

        assertContains(date)
        assertContains(distance)
        assertContains(getResourceString(R.string.mls))
        assertContains(SetupTestRunner.NEW_LAT.toString())
        assertContains(SetupTestRunner.NEW_LON.toString())
        assertContains(getResourceString(R.string.gps))
        assertContains(SetupTestRunner.LAT_1.toString())
        assertContains(SetupTestRunner.LON_1.toString())
        assertContains(accuracy)
    }

    @Test
    fun showMeasurementListTest() {

        for (m in SetupTestRunner.MEASUREMENT_LIST) {
            scrollListToPosition(R.id.listView, SetupTestRunner.MEASUREMENT_LIST.indexOf(m))
            assertContains(DATE_FORMAT.format(m.timestamp))
        }
    }

    @Test
    fun openMeasurementDetailsTest() {
        val measurement = SetupTestRunner.MEASUREMENT_LIST[POSITION_LIST_ITEM]
        val date = DATE_FORMAT.format(measurement.timestamp)
        val mlsLat = measurement.mlsResponse.location.lat
        val mlsLon = measurement.mlsResponse.location.lng
        val gpsLat = measurement.gpsLocation.lat
        val gpsLon = measurement.gpsLocation.lng
        val distance = DECIMAL_FORMAT.format(DistanceUtils.haversineDistance(mlsLat, mlsLon, gpsLat, gpsLon))
        val accuracy = DECIMAL_FORMAT.format(SetupTestRunner.ACCURACY)

        clickListItem(R.id.listView, POSITION_LIST_ITEM)

        assertContains(date)
        assertContains(distance)
        assertContains(getResourceString(R.string.mls))
        assertContains(mlsLat.toString())
        assertContains(mlsLon.toString())
        assertContains(getResourceString(R.string.gps))
        assertContains(gpsLat.toString())
        assertContains(gpsLon.toString())
        assertContains(accuracy)
    }

    @Test
    fun deleteMeasurementTest() {
        val measurement = SetupTestRunner.MEASUREMENT_LIST[POSITION_LIST_ITEM]
        val date = DATE_FORMAT.format(measurement.timestamp)

        assertContains(date)

        clickListItem(R.id.listView, POSITION_LIST_ITEM)
        assertContains(date)

        clickMenu(R.id.action_delete)
        assertNotContains(date)
    }

    private fun getResourceString(id: Int): String {
        val targetContext = InstrumentationRegistry.getTargetContext()
        return targetContext.resources.getString(id)
    }
}
