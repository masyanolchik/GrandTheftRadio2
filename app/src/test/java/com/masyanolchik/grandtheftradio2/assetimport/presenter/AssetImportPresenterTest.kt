package com.masyanolchik.grandtheftradio2.assetimport.presenter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.assetimport.AssetImportContract
import com.masyanolchik.grandtheftradio2.assetimport.model.AssetImportModelTest.Companion.SERIALIZED_STRING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import com.masyanolchik.grandtheftradio2.domain.Result
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class AssetImportPresenterTest {
    private val mockedAssetImportView = mock(AssetImportContract.View::class.java)
    private val mockedAssetImportModel = mock(AssetImportContract.Model::class.java)
    private lateinit var assetImportPresenter: AssetImportPresenter
    private val testCoroutineDispatcher = StandardTestDispatcher()
    @Before
    fun setup() {
        assetImportPresenter = AssetImportPresenter(
            mockedAssetImportModel,
            CoroutineScope(testCoroutineDispatcher),
            testCoroutineDispatcher
        )
        assetImportPresenter.setView(mockedAssetImportView)
    }

    @After
    fun stopDi() {
        stopKoin()
    }

    @Test
    fun testAssetImportPresenterTest_processImportedJsonString_completesWithSuccessHideProgress() = runTest {
        var hideText = ""
        val context = RuntimeEnvironment.getApplication().applicationContext
        `when`(mockedAssetImportView.getString(anyInt())).doAnswer {
            val resourceId = it.arguments[0] as Int
            return@doAnswer context.getString(resourceId)
        }
        doAnswer {
            hideText = it.arguments[0] as String
            null
        }.`when`(mockedAssetImportView).showImportResultStatus(any())
        `when`(mockedAssetImportModel.buildMediaTreeFromStationsList(any()))
            .thenReturn(flowOf(Result.Completed()))

        assetImportPresenter.processImportedJsonString(SERIALIZED_STRING)
        testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertThat(hideText).isNotEmpty()
        assertThat(hideText).isEqualTo(context.getString(R.string.import_tree_success))
    }

    @Test
    fun testAssetImportPresenterTest_processImportedJsonString_completesWithErrorHideProgress() = runTest {
        var hideText = ""
        val context = RuntimeEnvironment.getApplication().applicationContext
        `when`(mockedAssetImportView.getString(anyInt(), any())).doAnswer {
            val resourceId = it.arguments[0] as Int
            val arg = it.arguments[1] as Any
            return@doAnswer context.getString(resourceId, arg)
        }
        doAnswer {
            hideText = it.arguments[0] as String
            null
        }.`when`(mockedAssetImportView).showImportResultStatus(any())
        `when`(mockedAssetImportModel.buildMediaTreeFromStationsList(any()))
            .thenReturn(flowOf(Result.Error(Exception(""))))

        assetImportPresenter.processImportedJsonString(SERIALIZED_STRING)
        testCoroutineDispatcher.scheduler.advanceUntilIdle()

        assertThat(hideText).isNotEmpty()
        assertThat(hideText)
            .isEqualTo(
                context.getString(R.string.import_tree_error, Exception("").toString())
            )
    }

    @Test
    fun testAssetImportPresenterTest_onDetach_viewFieldIsNull() {
        val assetViewFieldBeforeOnDestroy =
            assetImportPresenter.javaClass.getDeclaredField("assetImportContractView")
        assetViewFieldBeforeOnDestroy.isAccessible = true
        val assetViewBeforeOnDestroy = assetViewFieldBeforeOnDestroy.get(assetImportPresenter)

        assetImportPresenter.onDetach()

        val assetViewFieldAfterOnDestroy =
            assetImportPresenter.javaClass.getDeclaredField("assetImportContractView")
        assetViewFieldAfterOnDestroy.isAccessible = true
        val assetViewAfterOnDestroy = assetViewFieldAfterOnDestroy.get(assetImportPresenter)

        assertThat(assetViewBeforeOnDestroy).isNotNull()
        assertThat(assetViewAfterOnDestroy).isNull()
    }
}