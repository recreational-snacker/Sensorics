package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class SavePublishDeviceJoinUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) : CompletableUseCaseWithParameter<PublishDeviceJoin> {

    override fun execute(parameter: PublishDeviceJoin): Completable {
        return Completable.fromAction {
            when (parameter) {
                is GooglePublishDeviceJoin -> publishDeviceJoinRepository.addGooglePublishDeviceJoin(
                    parameter
                )
                is RestPublishDeviceJoin -> publishDeviceJoinRepository.addRestPublishDeviceJoin(
                    parameter
                )
                is MqttPublishDeviceJoin -> publishDeviceJoinRepository.addMqttPublishDeviceJoin(
                    parameter
                )
                else -> throw IllegalArgumentException("Illegal argument provided")
            }
        }
    }
}