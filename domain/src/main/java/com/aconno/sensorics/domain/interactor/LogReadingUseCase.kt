package com.aconno.sensorics.domain.interactor

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Completable

class LogReadingUseCase(
        private val fileStorage: FileStorage
) : CompletableUseCaseWithParameter<List<Reading>> {

    private val GYRO_DPS_DIGIT_500DPS = 0.01750f
    private val ANGULAR_DISPL_NAME = "Angular Displacement"

    override fun execute(parameter: List<Reading>, previousValues: Array<Long>): Completable {
        for (reading in parameter) {
            logReading(reading)
            if ("gyroscope z".equals(reading.name.toLowerCase())) {
                val readingValue = reading.value.toFloat() * GYRO_DPS_DIGIT_500DPS
                if (0L == previousValues[0]) {
                    previousValues[0] = reading.timestamp
                    val firstReading = Reading(reading.timestamp, reading.device, previousValues[1], ANGULAR_DISPL_NAME)
                    logReading(firstReading)
                }

                val deltaTime = reading.timestamp - previousValues[0]
                println("deltaTime : "+deltaTime)
                val deltaVelocity = subtract(readingValue.toLong(), previousValues[1])
                println("deltaVel : "+deltaVelocity)
                val deltaDisplacement = deltaVelocity * deltaTime
                println(reading.name.toLowerCase() + " " + deltaDisplacement + " : Original values " + reading.value + " - " + previousValues[0])
                val angularReading = Reading(deltaTime, reading.device, deltaDisplacement, ANGULAR_DISPL_NAME)
                logReading(angularReading)
                previousValues[0] = reading.timestamp
                previousValues[1] = reading.value.toLong()
            }
        }
        return Completable.complete()
    }

    fun subtract(a: Long, b: Long): Long {
        return a - b
    }

    private fun logReading(reading: Reading) {
        val fileName = "${reading.name.toLowerCase()}.csv"
        fileStorage.storeReading(reading, fileName)
    }
}