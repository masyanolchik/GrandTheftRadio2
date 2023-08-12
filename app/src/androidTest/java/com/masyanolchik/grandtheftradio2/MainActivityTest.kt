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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = activityScenarioRule<MainActivity>()

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

        onView(ViewMatchers.withId(R.id.twod_dest)).perform(ViewActions.click())
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

        onView(ViewMatchers.withId(R.id.threed_dest)).perform(ViewActions.click())
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

        onView(ViewMatchers.withId(R.id.hd_dest)).perform(ViewActions.click())
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

        onView(ViewMatchers.withId(R.id.import_dest)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.toolbar)).check { view, _ ->
            val toolbar = (view as Toolbar)

            assertThat(toolbar.title).isEqualTo(toolbar.resources.getString(R.string.bottom_nav_title_import))
        }
        assertThat(navController?.currentDestination?.id).isEqualTo(R.id.import_dest)
    }
}
