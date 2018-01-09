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
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.schibsted.spain.barista.interaction.BaristaListInteractions.scrollListToPosition
import com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu


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
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    private val decimalFormat = DecimalFormat("#.##")

    @Before
    fun setUp() {
        baristaRule.launchActivity()
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_WIFI_STATE)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.INTERNET)
    }

    @Test
    fun makeMeasurementTest() {
        //TODO assert correct information after measurement mock
        val today = dateFormat.format(SetupTestRunner.DATE)
        val distance = decimalFormat.format(DistanceUtils.haversineDistance(SetupTestRunner.LAT_1, SetupTestRunner.LON_1, SetupTestRunner.LAT_2, SetupTestRunner.LON_2))

        clickOn(R.id.fab)

        //assertContains(today)
        //assertContains(distance)
        assertContains(getResourceString(R.string.mls))
        //assertContains(SetupTestRunner.LAT_2.toString())
        //assertContains(SetupTestRunner.LON_2.toString())
        assertContains(getResourceString(R.string.gps))
        //assertContains(SetupTestRunner.LAT_1.toString())
        //assertContains(SetupTestRunner.LON_1.toString())
    }

    @Test
    fun showMeasurementListTest() {
        onView(withId(R.id.listView)).check(ViewAssertions.matches(Matchers.withListSize(SetupTestRunner.MEASUREMENT_LIST_SIZE)))
        
        scrollListToPosition(R.id.listView, SetupTestRunner.MEASUREMENT_LIST_SIZE-1)
    }

    @Test
    fun openMeasurementDetailsTest() {
        val date = dateFormat.format(SetupTestRunner.DATE)
        val distance = decimalFormat.format(DistanceUtils.haversineDistance(SetupTestRunner.LAT_1, SetupTestRunner.LON_1, SetupTestRunner.LAT_2, SetupTestRunner.LON_2))

        clickListItem(R.id.listView, 0)

        assertContains(date)
        assertContains(distance)
        assertContains(getResourceString(R.string.mls))
        assertContains(SetupTestRunner.LAT_2.toString())
        assertContains(SetupTestRunner.LON_2.toString())
        assertContains(getResourceString(R.string.gps))
        assertContains(SetupTestRunner.LAT_1.toString())
        assertContains(SetupTestRunner.LON_1.toString())
    }

    @Test
    fun deleteMeasurementTest() {
        onView(withId(R.id.listView)).check(ViewAssertions.matches(Matchers.withListSize(SetupTestRunner.MEASUREMENT_LIST_SIZE)))

        clickListItem(R.id.listView, 0)
        clickMenu(R.id.action_delete)

        onView(withId(R.id.listView)).check(ViewAssertions.matches(Matchers.withListSize(SetupTestRunner.MEASUREMENT_LIST_SIZE-1)))
    }

    private fun getResourceString(id: Int): String {
        val targetContext = InstrumentationRegistry.getTargetContext()
        return targetContext.resources.getString(id)
    }
}
