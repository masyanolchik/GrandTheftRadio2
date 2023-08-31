package com.masyanolchik.grandtheftradio2.stations

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masyanolchik.grandtheftradio2.KoinTestRule
import com.masyanolchik.grandtheftradio2.RadioApplication
import com.masyanolchik.grandtheftradio2.stations.model.StationModel
import com.masyanolchik.grandtheftradio2.stations.presenter.StationPresenter
import com.masyanolchik.grandtheftradio2.stationstree.repository.StationsRepository
import com.masyanolchik.grandtheftradio2.stationstree.repository.testing.FakeStationRepositoryImpl
import com.masyanolchik.grandtheftradio2.stationstree.repository.testing.FakeStationRepositoryImpl.Companion.FAKE_STATIONS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import com.masyanolchik.grandtheftradio2.R
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After

@RunWith(AndroidJUnit4::class)
class StationsFragmentTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()
    private val stationsRepository = FakeStationRepositoryImpl()
    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            RadioApplication.stationsTreeModule,
            module {
                scope<StationsFragment> {
                    scoped {
                        StationPresenter(
                            StationModel(get()),
                            CoroutineScope(testCoroutineDispatcher),
                            testCoroutineDispatcher
                        ) as StationContract.Presenter
                    }
                }
            },
            module {
                single<StationsRepository> { stationsRepository }
            }
        )
    )

    @After
    fun resetRepository() {
        stationsRepository.isThrowingErrors = false
        stationsRepository.nukeDatabase()
    }

    @Test
    fun testStationsFragment_showsTileFor2D() {
        stationsRepository.saveStations(FAKE_STATIONS)
        val args = Bundle().apply {
            putString("eraName", "2D")
        }
        launchFragmentInContainer<StationsFragment>(args).onFragment {
            testCoroutineDispatcher.scheduler.advanceUntilIdle()
        }

        onView(withText("GameName1")).check(matches(isDisplayed()))
        onView(withText("station name1")).check(matches(isDisplayed()))
    }

    @Test
    fun testStationsFragment_showsTileFor3D() {
        stationsRepository.saveStations(FAKE_STATIONS)
        val args = Bundle().apply {
            putString("eraName", "3D")
        }
        launchFragmentInContainer<StationsFragment>(args).onFragment {
            testCoroutineDispatcher.scheduler.advanceUntilIdle()
        }

        onView(withText("GameName2")).check(matches(isDisplayed()))
        onView(withText("station name2")).check(matches(isDisplayed()))
    }

    @Test
    fun testStationsFragment_showsTileForHD() {
        stationsRepository.saveStations(FAKE_STATIONS)
        val args = Bundle().apply {
            putString("eraName", "HD")
        }
        launchFragmentInContainer<StationsFragment>(args).onFragment {
            testCoroutineDispatcher.scheduler.advanceUntilIdle()
        }

        onView(withText("GameName3")).check(matches(isDisplayed()))
        onView(withText("station name3")).check(matches(isDisplayed()))
    }

    @Test
    fun testStationsFragment_showsErrorText() {
        stationsRepository.isThrowingErrors = true
        val args = Bundle().apply {
            putString("eraName", "2D")
        }
        launchFragmentInContainer<StationsFragment>(args).onFragment {
            testCoroutineDispatcher.scheduler.advanceUntilIdle()
        }

        onView(withText(R.string.station_error_text)).check(matches(isDisplayed()))
    }


    @Test
    fun testStationFragment_showsLoadingProgressBar() {
        val args = Bundle().apply {
            putString("eraName", "2D")
        }

        launchFragmentInContainer<StationsFragment>(args)

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }
}