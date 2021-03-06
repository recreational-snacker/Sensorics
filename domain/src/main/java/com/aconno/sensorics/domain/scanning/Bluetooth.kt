package com.aconno.sensorics.domain.scanning

import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.model.ScanEvent
import io.reactivex.Flowable
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.GattCallbackPayload
import java.util.*

interface Bluetooth {

    fun enable()

    fun disable()

    fun startScanning()

    fun startScanning(devices: List<Device>)

    fun stopScanning()

    fun connect(address: String)

    fun disconnect()

    fun closeConnection()

    fun getScanResults(): Flowable<ScanResult>

    fun getScanEvents(): Flowable<ScanEvent>

    fun getStateEvents(): Flowable<BluetoothState>

    fun getGattResults(): Flowable<GattCallbackPayload>

    fun readCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID
    ): Boolean

    fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        type: String,
        value: Any
    ): Boolean
}