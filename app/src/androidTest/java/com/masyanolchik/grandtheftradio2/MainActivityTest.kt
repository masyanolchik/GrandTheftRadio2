package com.masyanolchik.grandtheftradio2

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.stations.StationContract
import com.masyanolchik.grandtheftradio2.stations.StationsFragment
import com.masyanolchik.grandtheftradio2.stations.model.StationModel
import com.masyanolchik.grandtheftradio2.stations.presenter.StationPresenter
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import com.masyanolchik.grandtheftradio2.stationstree.repository.testing.FakeStationRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = activityScenarioRule<MainActivity>()

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun testNavigation_homeDestIsFavorites() {
        var navController: NavController? = null

        rule.scenario.onActivity { activity ->
            navController = (activity.supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment).navController
        }
        rule.scenario.moveToState(Lifecycle.State.RESUMED)

        assertThat(navController?.currentDestination?.id).isEqualTo(R.id.fav_dest)
    }

    @Test
    fun testNavigation_switchesToTwoD() {
        var navController: NavController? = null

        rule.scenario.onActivity { activity ->
            navController = (activity.supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment).navController
        }
        rule.scenario.moveToState(Lifecycle.State.RESUMED)

        onView(ViewMatchers.withId(R.id.twod_dest_menu)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.toolbar)).check { view, _ ->
            val toolbar = (view as Toolbar)

            assertThat(toolbar.title).isEqualTo(toolbar.resources.getString(R.string.bottom_nav_title_2d))
        }
        assertThat(navController?.currentDestination?.id).isEqualTo(R.id.stations_dest)
    }

    @Test
    fun testNavigation_switchesToThreeD() {
        var navController: NavController? = null

        rule.scenario.onActivity { activity ->
            navController = (activity.supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment).navController
        }
        rule.scenario.moveToState(Lifecycle.State.RESUMED)

        onView(ViewMatchers.withId(R.id.threed_dest_menu)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.toolbar)).check { view, _ ->
            val toolbar = (view as Toolbar)

            assertThat(toolbar.title).isEqualTo(toolbar.resources.getString(R.string.bottom_nav_title_3d))
        }
        assertThat(navController?.currentDestination?.id).isEqualTo(R.id.stations_dest)
    }

    @Test
    fun testNavigation_switchesToHD() {
        var navController: NavController? = null

        rule.scenario.onActivity { activity ->
            navController = (activity.supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment).navController
        }
        rule.scenario.moveToState(Lifecycle.State.RESUMED)

        onView(ViewMatchers.withId(R.id.hd_dest_menu)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.toolbar)).check { view, _ ->
            val toolbar = (view as Toolbar)

            assertThat(toolbar.title).isEqualTo(toolbar.resources.getString(R.string.bottom_nav_title_hd))
        }
        assertThat(navController?.currentDestination?.id).isEqualTo(R.id.stations_dest)
    }

    @Test
    fun testNavigation_switchesToImport() {
        var navController: NavController? = null

        rule.scenario.onActivity { activity ->
            navController = (activity.supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment).navController
        }
        rule.scenario.moveToState(Lifecycle.State.RESUMED)

        onView(ViewMatchers.withId(R.id.import_dest_menu)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.toolbar)).check { view, _ ->
            val toolbar = (view as Toolbar)

            assertThat(toolbar.title).isEqualTo(toolbar.resources.getString(R.string.bottom_nav_title_import))
        }
        assertThat(navController?.currentDestination?.id).isEqualTo(R.id.import_dest)
    }
}
