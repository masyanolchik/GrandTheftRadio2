package com.masyanolchik.grandtheftradio2.assetimport

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.masyanolchik.grandtheftradio2.KoinTestRule
import com.masyanolchik.grandtheftradio2.R
import com.masyanolchik.grandtheftradio2.RadioApplication
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.TimeoutException


private const val BASIC_SAMPLE_PACKAGE = "com.masyanolchik.grandtheftradio2"
private const val LAUNCH_TIMEOUT = 5000L
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ImportFragmentTest {
    private val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private lateinit var context: Context

    @get:Rule
    val koinTestRule = KoinTestRule()

    @Before
    fun startImportFragmentScreen() {
        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = device.launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        // Launch the app
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(
            BASIC_SAMPLE_PACKAGE)?.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )
    }

    @Test
    fun a_testImportFragment_importTemplate_showsPermissionDialog() {
        // Revoking permissions
        device.executeShellCommand("pm reset-permissions")
        device.executeShellCommand("rm /sdcard/Download/stations_template.json")

        onView(allOf(withText(R.string.import_title), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())
        onView(withText(R.string.import_template_from_storage)).perform(click())
        assertViewWithTextIsVisible(device, "ALLOW")
        assertViewWithTextIsVisible(device, "DENY")
    }


    @Test
    fun b_testImportFragment_copyTemplate_showsPermissionDialog() {
        onView(allOf(withText(R.string.import_title), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())
        onView(withText(R.string.copy_template_to_downloads)).perform(click())
        assertViewWithTextIsVisible(device, "ALLOW")
        assertViewWithTextIsVisible(device, "DENY")
    }

    @Test
    fun c_testImportFragment_copyTemplate_denyPermissionShowsRatio() {
        onView(allOf(withText(R.string.import_title), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())
        onView(withText(R.string.copy_template_to_downloads)).perform(click())
        clickViewWithText(device, "DENY")
        onView(withText(R.string.copy_template_to_downloads)).perform(click())
        onView(withText(R.string.write_file_permission_ratio))
            .check(matches(isDisplayed()))
    }

    @Test
    fun d_testImportFragment_copyTemplate_happyPathSucceedsAfterDenial() {
        onView(allOf(withText(R.string.import_title), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())
        onView(withText(R.string.copy_template_to_downloads)).perform(click())
        onView(withText(R.string.grant_permission_action)).perform(click())
        clickViewWithText(device, "ALLOW")
        onView(withText(R.string.copy_template_to_downloads)).perform(click())
        onView(withText(R.string.template_is_in_your_downloads_folder))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e_testImportFragment_importTemplate_happyPathSucceeds() {
        onView(allOf(withText(R.string.import_title), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click())
        onView(withText(R.string.import_template_from_storage)).perform(click())
        clickViewWithContentDesc(device, "Show roots")
        clickViewWithText(device, "Android SDK built for arm64")
        clickViewWithText(device, "Download")
        clickViewWithText(device, "stations_template.json")
        onView(isRoot()).perform(waitText(context.getString(R.string.import_tree_success), 20000L))
    }

    private fun waitText(viewText: String, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id <$viewText> during $millis millis."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher: Matcher<View> = withText(viewText)
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
            }
        }
    }

    companion object {
        fun clickViewWithText(device: UiDevice, text: String) {
            val view = device.findObject(UiSelector().text(text))
            view.click()
        }

        fun clickViewWithContentDesc(device: UiDevice, text: String) {
            val view = device.findObject(UiSelector().descriptionContains(text))
            view.click()
        }

        fun assertViewWithTextIsVisible(device: UiDevice, text: String) {
            val view = device.findObject(UiSelector().text(text))
            if (!view.exists()) {
                throw AssertionError("View with text <$text> not found!")
            }
        }
    }
}