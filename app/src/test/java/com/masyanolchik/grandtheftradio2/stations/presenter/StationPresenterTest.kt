package com.masyanolchik.grandtheftradio2.stations.presenter

import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.stations.StationContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.masyanolchik.grandtheftradio2.domain.Result
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.any

@RunWith(MockitoJUnitRunner::class)
class StationPresenterTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()
    private lateinit var stationPresenter: StationPresenter
    private val mockedStationView = mock(StationContract.View::class.java)
    private val mockedStationModel = mock(StationContract.Model::class.java)

    @Before
    fun setup() {
        stationPresenter = StationPresenter(
            mockedStationModel,
            CoroutineScope(testCoroutineDispatcher),
            testCoroutineDispatcher
        )
    }

    @After
    fun stopDi() {
        stopKoin()
    }

    @Test
    fun testStationPresenter_setView_viewFieldIsNotNull() {
        val stationViewField =
            stationPresenter.javaClass.getDeclaredField("stationContractView")
        stationViewField.isAccessible = true

        stationPresenter.setView(mockedStationView)
        val stationViewAfterSetView = stationViewField.get(stationPresenter)

        assertThat(stationViewAfterSetView).isNotNull()
    }

    @Test
    fun testStationPresenter_prepareItemsForEra_completesWithSuccessViewNull() = runTest {
        Mockito.`when`(mockedStationModel.getItemsForEra(anyString()))
            .thenReturn(flowOf(Result.Success(emptyList())))

        stationPresenter.prepareItemsForEra("2D")
        testCoroutineDispatcher.scheduler.advanceUntilIdle()

        verify(mockedStationView, times(0)).showLoadingProgress()
        verify(mockedStationModel, times(1)).getItemsForEra(anyString())
        verify(mockedStationView, times(0)).hideLoadingProgress()
        verify(mockedStationView, times(0)).updateList(any())
    }

    @Test
    fun testStationPresenter_prepareItemsForEra_completesWithSuccess() = runTest {
        Mockito.`when`(mockedStationModel.getItemsForEra(anyString()))
            .thenReturn(flowOf(Result.Success(emptyList())))

        stationPresenter.setView(mockedStationView)
        stationPresenter.prepareItemsForEra("2D")
        testCoroutineDispatcher.scheduler.advanceUntilIdle()

        verify(mockedStationView, times(1)).showLoadingProgress()
        verify(mockedStationModel, times(1)).getItemsForEra(anyString())
        verify(mockedStationView, times(1)).hideLoadingProgress()
        verify(mockedStationView, times(1)).updateList(any())
    }

    @Test
    fun testStationPresenter_prepareItemsForEra_completesWithError() = runTest {
        Mockito.`when`(mockedStationModel.getItemsForEra(anyString()))
            .thenReturn(flowOf(Result.Error(Exception())))

        stationPresenter.setView(mockedStationView)
        stationPresenter.prepareItemsForEra("2D")
        testCoroutineDispatcher.scheduler.advanceUntilIdle()

        verify(mockedStationView, times(1)).showLoadingProgress()
        verify(mockedStationModel, times(1)).getItemsForEra(anyString())
        verify(mockedStationView, times(1)).hideLoadingProgress()
        verify(mockedStationView, times(1)).showErrorScreen()
    }

    @Test
    fun testStationPresenter_onDetach_viewFieldIsNull() {
        val stationViewField =
            stationPresenter.javaClass.getDeclaredField("stationContractView")
        stationViewField.isAccessible = true

        stationPresenter.setView(mockedStationView)
        val stationViewBeforeOnDetach = stationViewField.get(stationPresenter)
        stationPresenter.onDetach()
        val stationViewAfterOnDetach = stationViewField.get(stationPresenter)

        assertThat(stationViewBeforeOnDetach).isNotNull()
        assertThat(stationViewAfterOnDetach).isNull()
    }
}