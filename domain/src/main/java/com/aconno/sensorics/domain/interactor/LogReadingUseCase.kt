package com.aconno.sensorics.domain.interactor

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Completable

class LogReadingUseCase(
        private val fileStorage: FileStorage
) : CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            logReading(reading)
        }
        return Completable.complete()
    }

    fun subtract(a: Long, b: Long): Long {
        return a - b
    }

    fun logReading(reading: Reading) {
        val fileName = "${reading.name.toLowerCase()}.csv"
        fileStorage.storeReading(reading, fileName)
    }

    fun overrideAndLogReading(reading: Reading) {
        val fileName = "${reading.name.toLowerCase()}.csv"
        fileStorage.overrideAndStoreReading(reading, fileName)
    }
}