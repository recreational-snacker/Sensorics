package com.aconno.acnsensa.dagger

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.BluetoothScanningServiceReceiver
import com.aconno.acnsensa.data.repository.InMemoryRepositoryImpl
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.domain.interactor.bluetooth.RecordSensorValuesUseCase
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class BluetoothScanningServiceModule(
    private val bluetoothScanningService: BluetoothScanningService
) {

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningService() = bluetoothScanningService

    @Provides
    @BluetoothScanningServiceScope
    fun provideNotification(): Notification {
        val notificationFactory = NotificationFactory()
        return notificationFactory.makeForegroundServiceNotification(bluetoothScanningService)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningServiceReceiver(): BroadcastReceiver =
        BluetoothScanningServiceReceiver(bluetoothScanningService)

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.acnsensa.STOP")

    @Provides
    @BluetoothScanningServiceScope
    fun provideRecordSensorValuesUseCase(): RecordSensorValuesUseCase {
        val inMemoryRepository = InMemoryRepositoryImpl()
        return RecordSensorValuesUseCase(inMemoryRepository)
    }
}