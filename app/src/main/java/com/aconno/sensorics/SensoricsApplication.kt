package com.aconno.sensorics

import android.app.Application
import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.dagger.application.AppModule
import com.aconno.sensorics.dagger.application.DaggerAppComponent
import com.aconno.sensorics.model.mapper.AdvertisementFormatMapper
import com.crashlytics.android.Crashlytics
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class SensoricsApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
        Fabric.with(this, Crashlytics())

        val mapper = AdvertisementFormatMapper()
        val reader = AdvertisementFormatReader()
        reader.readFlowable(this)
            .subscribe {
                appComponent = DaggerAppComponent
                    .builder()
                    .appModule(AppModule(this, it.map { mapper.toAdvertisementFormat(it) }))
                    .build()
            }
    }
}