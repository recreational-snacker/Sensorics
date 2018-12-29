package com.aconno.sensorics

import android.app.Application
import android.hardware.Sensor
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


//        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//
//        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
//            this.accelerometer = it
//        }
//
//        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.let {
//            this.gravity = it
//        }
//
//        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
//            this.gyroscope = it
//        }
//
//        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
//            this.linearAcceleration = it
//        }
//
//        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
//            this.rotationVector = it
//        }

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
