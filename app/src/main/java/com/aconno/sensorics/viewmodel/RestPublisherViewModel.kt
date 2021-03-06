package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.RESTHeaderModel
import com.aconno.sensorics.model.RESTHttpGetParamModel
import com.aconno.sensorics.model.RESTPublishModel
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class RestPublisherViewModel(
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
    private val getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,
    private val saveRESTHttpGetParamUseCase: SaveRESTHttpGetParamUseCase,
    private val getRESTHttpGetParamsByIdUseCase: GetRESTHttpGetParamsByIdUseCase,
    private val restHttpGetParamModelMapper: RESTHttpGetParamModelMapper
) : ViewModel() {

    fun save(
        restPublishModel: RESTPublishModel
    ): Single<Long> {

        val transform = restPublishModelDataMapper.transform(restPublishModel)
        return addRESTPublishUseCase.execute(transform)
    }

    fun addOrUpdateRestRelation(
        deviceId: String,
        restId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
                deviceId
            )
        )
    }

    fun deleteRelationRest(
        deviceId: String,
        restId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
                deviceId
            )
        )
    }

    fun addRESTHeader(
        list: List<RESTHeaderModel>,
        it: Long
    ): Completable {
        return saveRESTHeaderUseCase.execute(
            restHeaderModelMapper.toRESTHeaderListByRESTPublishId(list, it)
        )
    }

    fun addRESTHttpGetParams(
        list: List<RESTHttpGetParamModel>,
        it: Long
    ): Completable {
        return saveRESTHttpGetParamUseCase.execute(
            restHttpGetParamModelMapper.toRESTHttpGetParamListByRESTPublishId(list, it)
        )
    }

    fun getRESTHeadersById(rId: Long): Maybe<List<RESTHeaderModel>> {
        return getRESTHeadersByIdUseCase.execute(rId)
            .map(restHeaderModelMapper::toRESTHeaderModelList)
    }

    fun getRESTHttpGetParamsById(rId: Long): Maybe<List<RESTHttpGetParamModel>> {
        return getRESTHttpGetParamsByIdUseCase.execute(rId)
            .map(restHttpGetParamModelMapper::toRESTHttpGetParamModelList)
    }

    fun checkFieldsAreEmpty(
        vararg strings: String
    ): Boolean {

        strings.forEach {
            if (it.isBlank()) {
                return true
            }
        }

        return false
    }
}