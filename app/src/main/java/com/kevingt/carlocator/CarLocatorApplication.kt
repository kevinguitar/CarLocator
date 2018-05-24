package com.kevingt.carlocator

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.kevingt.carlocator.data.AppPreferenceManager
import io.fabric.sdk.android.Fabric

class CarLocatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        AppPreferenceManager.getInstance(applicationContext)
    }

}