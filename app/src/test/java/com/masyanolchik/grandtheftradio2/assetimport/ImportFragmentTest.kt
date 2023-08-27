package com.masyanolchik.grandtheftradio2.assetimport

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masyanolchik.grandtheftradio2.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.declare
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RuntimeEnvironment
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class ImportFragmentTest: KoinTest {
    private val mockedPresenter: AssetImportContract.Presenter = mock()
    private val context: Context = RuntimeEnvironment.getApplication()
    @Before
    fun setup() {
        context.setTheme(R.style.Theme_GrandTheftRadio2)
        val templateBytes = context.assets.open("stations.json").readBytes()

        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val template = File(downloadsDirectory, "station_template.json")
       FileOutputStream(template).use {
           it.write(templateBytes)
       }
        val registry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, ActivityResult(AppCompatActivity.RESULT_OK, Intent().apply {
                    data = Uri.fromFile(template)
                }))
            }
        }
        stopKoin()
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(module {
                single { registry } binds arrayOf(ActivityResultRegistry::class)
                scope<ImportFragment> {
                    scoped {
                        mockedPresenter
                    }
                }
            })
        }
    }

    @After
    fun stopDi() {
        stopKoin()
    }

    @Test
    fun testImportFragment_fileLocationLauncherReceivesUri() {
        with(launchFragmentInContainer<ImportFragment>()) {
            moveToState(Lifecycle.State.RESUMED)

            onFragment { _ ->
                onView(withText(R.string.import_template_from_storage)).perform(click())

                verify(mockedPresenter, times(1)).processImportedJsonString(any())
            }
        }

    }
}