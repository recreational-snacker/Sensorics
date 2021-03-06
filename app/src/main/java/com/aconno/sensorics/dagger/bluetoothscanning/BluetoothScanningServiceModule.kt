package com.aconno.sensorics.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BluetoothScanningServiceReceiver
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.device.storage.FileStorageImpl
import com.aconno.sensorics.domain.SmsSender
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.outcome.*
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.GetAllEnabledGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.GetAllEnabledMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.UpdateMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.GetAllEnabledRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.repository.InMemoryRepository
import dagger.Module
import dagger.Provides

@Module
class BluetoothScanningServiceModule(
    private val bluetoothScanningService: BluetoothScanningService
) {

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningService() = bluetoothScanningService

    @Provides
    @BluetoothScanningServiceScope
    fun provideNotification(
        sensoricsApplication: SensoricsApplication,
        intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = bluetoothScanningService.getString(R.string.service_notification_title)
        val content = bluetoothScanningService.getString(R.string.service_notification_content)
        return notificationFactory.makeForegroundServiceNotification(
            bluetoothScanningService,
            intentProvider.getSensoricsContentIntent(sensoricsApplication),
            title,
            content
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningServiceReceiver(): BroadcastReceiver =
        BluetoothScanningServiceReceiver(bluetoothScanningService)

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.sensorics.STOP")

    @Provides
    @BluetoothScanningServiceScope
    fun provideRecordSensorValuesUseCase(
        inMemoryRepository: InMemoryRepository
    ): SaveSensorReadingsUseCase {
        return SaveSensorReadingsUseCase(inMemoryRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideLogReadingsUseCase(): LogReadingUseCase {
        return LogReadingUseCase(FileStorageImpl(bluetoothScanningService))
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideHandleInputUseCase(actionsRepository: ActionsRepository): InputToOutcomesUseCase {
        return InputToOutcomesUseCase(actionsRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideRunOutcomeUseCase(
        notificationDisplay: NotificationDisplay,
        smsSender: SmsSender,
        textToSpeechPlayer: TextToSpeechPlayer,
        vibrator: Vibrator
    ): RunOutcomeUseCase {
        val notificationOutcomeExecutor = NotificationOutcomeExecutor(notificationDisplay)
        val smsOutcomeExecutor = SmsOutcomeExecutor(smsSender)
        val textToSpeechOutcomeExecutor = TextToSpeechOutcomeExecutor(textToSpeechPlayer)
        val vibrationOutcomeExecutor = VibrationOutcomeExecutor(vibrator)
        val outcomeExecutorSelector = OutcomeExecutorSelector(
            notificationOutcomeExecutor,
            smsOutcomeExecutor,
            textToSpeechOutcomeExecutor,
            vibrationOutcomeExecutor
        )

        return RunOutcomeUseCase(outcomeExecutorSelector)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetAllEnabledGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllEnabledGooglePublishUseCase {
        return GetAllEnabledGooglePublishUseCase(
            googlePublishRepository
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetAllEnabledRESTPublishUseCase(restPublishRepository: RESTPublishRepository): GetAllEnabledRESTPublishUseCase {
        return GetAllEnabledRESTPublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetAllEnabledMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): GetAllEnabledMqttPublishUseCase {
        return GetAllEnabledMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideUpdateRESTPublishUseCase(restPublishRepository: RESTPublishRepository): UpdateRESTPublishUserCase {
        return UpdateRESTPublishUserCase(
            restPublishRepository
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideUpdateGooglePublishUseCase(restPublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(
            restPublishRepository
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideUpdateMqttPublishUseCase(mqttPublishRepository: MqttPublishRepository): UpdateMqttPublishUseCase {
        return UpdateMqttPublishUseCase(
            mqttPublishRepository
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithRESTPublishUseCase {
        return GetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithMqttPublishUseCase {
        return GetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetRESTHeadersByIdUseCase(restPublishRepository: RESTPublishRepository): GetRESTHeadersByIdUseCase {
        return GetRESTHeadersByIdUseCase(restPublishRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetRESTHttpGetParamsByIdUseCase(restPublishRepository: RESTPublishRepository): GetRESTHttpGetParamsByIdUseCase {
        return GetRESTHttpGetParamsByIdUseCase(restPublishRepository)
    }
}