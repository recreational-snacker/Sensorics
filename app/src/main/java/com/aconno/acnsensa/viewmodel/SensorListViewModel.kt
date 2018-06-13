package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.interactor.filter.Reading
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

class SensorListViewModel(
    private val readingsStream: Flowable<List<Reading>>,
    private val filterByMacUseCase: FilterByMacUseCase
) : ViewModel() {

    private val sensorValuesLiveData: MutableLiveData<Map<String, Number>> = MutableLiveData()

    private var disposable: Disposable? = null

    fun setMacAddress(macAddress: String) {
        disposable?.dispose()
        disposable = readingsStream
            .concatMap { filterByMacUseCase.execute(it, macAddress).toFlowable() }
            .subscribe { processSensorValues(it) }
    }

    private fun processSensorValues(values: List<Reading>) {
        sensorValuesLiveData.value = values.associateBy({ it.type.toString() }, { it.value })
    }

    fun getSensorValuesLiveData(): MutableLiveData<Map<String, Number>> {
        return sensorValuesLiveData
    }
}