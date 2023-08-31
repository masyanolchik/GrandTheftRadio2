package com.masyanolchik.grandtheftradio2

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class InstrumentationTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }

    class TestApplication: Application() {
        override fun onCreate() {
            super.onCreate()
            setTheme(R.style.Base_Theme_GrandTheftRadio2)
        }
    }
}