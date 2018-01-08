package at.ac.tuwien.mns.mnsgeolocation

import android.Manifest
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.runner.AndroidJUnit4
import at.ac.tuwien.mns.mnsgeolocation.dto.MeasurementDao
import at.ac.tuwien.mns.mnsgeolocation.service.MLSLocationService
import at.ac.tuwien.mns.mnsgeolocation.service.ManagerService
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import com.schibsted.spain.barista.interaction.PermissionGranter

import org.mockito.Mockito.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Rule
import com.schibsted.spain.barista.rule.cleardata.ClearDatabaseRule





/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GeolocationBaristaTest {

    @Rule @JvmField
    var baristaRule = BaristaRule.create(MeasurementActivity::class.java)
    var mockedManagerService : ManagerService = mock(ManagerService::class.java)
    var mockedMLSLocationService : MLSLocationService = mock(MLSLocationService::class.java)
    var mockedMeasurementDAO : MeasurementDao = mock(MeasurementDao::class.java)

    @Before
    fun setUp() {
        baristaRule.launchActivity()
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_WIFI_STATE)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.INTERNET)
    }

    @Test
    fun makeMeasurementTest() {
        clickOn(R.id.fab)
        sleep(2000)
        assertDisplayed("Global")
    }

    @Test
    fun showMeasurementListTest() {

    }

    @Test
    fun openMeasurementDetailsTest() {

    }

    @Test
    fun deleteMeasurementTest() {

    }
}
