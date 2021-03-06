package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.viewmodel.DeviceViewModel
import io.reactivex.Flowable

class DeviceListViewModelFactory(
    private val deviceStream: Flowable<Device>,
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase,
    private val saveDevicesUseCase: SaveDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = DeviceViewModel(
            deviceStream,
            getSavedDevicesUseCase,
            saveDevicesUseCase,
            deleteDeviceUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}