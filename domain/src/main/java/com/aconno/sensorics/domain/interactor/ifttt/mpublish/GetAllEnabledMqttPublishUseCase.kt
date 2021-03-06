package com.aconno.sensorics.domain.interactor.ifttt.mpublish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledMqttPublishUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return mqttPublishRepository.getAllEnabledMqttPublish()
    }
}