package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.rpublish.RESTPublishEntity
import com.aconno.sensorics.domain.ifttt.GeneralRESTPublish
import com.aconno.sensorics.domain.ifttt.RESTPublish
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RESTPublishEntityDataMapper @Inject constructor() {

    /**
     * Transform a [RESTPublishEntity] into an [RESTPublish].
     *
     * @param restPublishEntity Object to be transformed.
     * @return [RESTPublish]
     */
    fun transform(restPublishEntity: RESTPublishEntity): RESTPublish {
        return GeneralRESTPublish(
            restPublishEntity.id,
            restPublishEntity.name,
            restPublishEntity.url,
            restPublishEntity.method,
            restPublishEntity.enabled,
            restPublishEntity.timeType,
            restPublishEntity.timeMillis,
            restPublishEntity.lastTimeMillis,
            restPublishEntity.dataString

        )
    }

    /**
     * Transform a List of [RESTPublishEntity] into a Collection of [RESTPublish].
     *
     * @param restPublishEntityCollection Object Collection to be transformed.
     * @return [RESTPublish]
     */
    fun transform(restPublishEntityCollection: Collection<RESTPublishEntity>): List<RESTPublish> {
        val restPublishList = mutableListOf<RESTPublish>()
        for (restPublishEntity in restPublishEntityCollection) {
            val user = transform(restPublishEntity)
            restPublishList.add(user)
        }
        return restPublishList
    }
}