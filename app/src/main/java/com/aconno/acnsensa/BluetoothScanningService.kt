package com.aconno.acnsensa

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.dagger.bluetoothscanning.BluetoothScanningServiceComponent
import com.aconno.acnsensa.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.acnsensa.dagger.bluetoothscanning.DaggerBluetoothScanningServiceComponent
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.ifttt.HandleInputUseCase
import com.aconno.acnsensa.domain.ifttt.ReadingToInputUseCase
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.repository.RecordSensorValuesUseCase
import com.aconno.acnsensa.domain.interactor.repository.SensorValuesToReadingsUseCase
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author aconno
 */
class BluetoothScanningService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    lateinit var sensorValues: Flowable<Map<String, Number>>

    @Inject
    lateinit var recordUseCase: RecordSensorValuesUseCase

    @Inject
    lateinit var sensorValuesToReadingsUseCase: SensorValuesToReadingsUseCase

    @Inject
    lateinit var logReadingsUseCase: LogReadingUseCase

    @Inject
    lateinit var readingToInputUseCase: ReadingToInputUseCase

    @Inject
    lateinit var handleInputUseCase: HandleInputUseCase

    @Inject
    lateinit var receiver: BroadcastReceiver

    @Inject
    lateinit var filter: IntentFilter

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    private val bluetoothScanningServiceComponent: BluetoothScanningServiceComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerBluetoothScanningServiceComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .bluetoothScanningServiceModule(
                BluetoothScanningServiceModule(
                    this
                )
            )
            .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothScanningServiceComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        localBroadcastManager.registerReceiver(receiver, filter)

        startForeground(1, notification)

        bluetooth.startScanning()
        running = true
        startRecording()
        startLogging()
        handleInputsForActions()
        return START_STICKY
    }

    private fun handleInputsForActions() {
        sensorValues
            .concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .concatMap { readingToInputUseCase.execute(it).toFlowable() }
            .flatMapIterable { it }
            .subscribe {
                handleInputUseCase.execute(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }
    }

    fun stopScanning() {
        bluetooth.stopScanning()
        running = false
        stopSelf()
    }

    private fun startRecording() {
        sensorValues.concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .subscribe {
                recordUseCase.execute(it)
            }
    }

    private fun startLogging() {
        sensorValues.concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .subscribe {
                logReadingsUseCase.execute(it)
            }
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, BluetoothScanningService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private var running = false

        fun isRunning(): Boolean {
            return running
        }
    }
}