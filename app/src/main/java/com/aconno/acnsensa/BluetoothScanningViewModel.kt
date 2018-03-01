package com.aconno.acnsensa

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.model.ScanEvent
import io.reactivex.Flowable
import timber.log.Timber

//TODO: This needs refactoring.
/**
 * @aconno
 */
class BluetoothScanningViewModel(
    private val bluetooth: Bluetooth, application: AcnSensaApplication
) : AndroidViewModel(application) {

    private val result: MutableLiveData<ScanEvent> = MutableLiveData()

    init {
        subscribe()
    }

    private fun subscribe() {
        val observable: Flowable<ScanEvent> = bluetooth.getScanEvents()
        observable.subscribe { result.value = it }
    }

    fun startScanning() {
        Timber.d("startScanning")
        BluetoothScanningService.start(getApplication())
    }

    fun stopScanning() {
        Timber.d("stopScanning")

        val localBroadcastManager = LocalBroadcastManager.getInstance(getApplication())
        localBroadcastManager.sendBroadcast(Intent("com.aconno.acnsensa.STOP"))
    }

    fun getResult(): MutableLiveData<ScanEvent> {
        return result
    }
}