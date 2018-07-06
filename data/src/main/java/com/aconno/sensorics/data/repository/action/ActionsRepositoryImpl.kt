package com.aconno.sensorics.data.repository.action

import com.aconno.sensorics.domain.ifttt.Action
import com.aconno.sensorics.domain.ifttt.ActionsRepository
import com.aconno.sensorics.domain.ifttt.GeneralAction
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.ifttt.outcome.Outcome
import io.reactivex.Single

class ActionsRepositoryImpl(
    private val actionDao: ActionDao
) : ActionsRepository {
    override fun addAction(action: Action) {
        actionDao.insert(toEntity(action))
    }

    override fun updateAction(action: Action) {
        actionDao.update(toEntity(action))
    }

    override fun deleteAction(action: Action) {
        actionDao.delete(toEntity(action))
    }

    override fun getAllActions(): Single<List<Action>> {
        return actionDao.all.map { actionEntities -> actionEntities.map { toAction(it) } }
    }

    override fun getActionById(actionId: Long): Single<Action> {
        return actionDao.getActionById(actionId).map { actionEntity -> toAction(actionEntity) }
    }

    private fun toEntity(action: Action): ActionEntity {
        val id = action.id
        val name = action.name
        val deviceMacAddress = action.deviceMacAddress
        val readingType = action.condition.readingType
        val conditionType = action.condition.type
        val value = action.condition.limit

        val type = action.outcome.type
        val message = action.outcome.parameters[Outcome.TEXT_MESSAGE] ?: "null"
        val number = action.outcome.parameters[Outcome.PHONE_NUMBER] ?: ""

        return ActionEntity(
            id = id,
            name = name,
            deviceMacAddress = deviceMacAddress,
            readingType = readingType,
            conditionType = conditionType,
            value = value,
            textMessage = message,
            outcomeType = type,
            phoneNumber = number
        )
    }

    private fun toAction(actionEntity: ActionEntity): Action {
        val id = actionEntity.id
        val name = actionEntity.name
        val deviceMacAddress = actionEntity.deviceMacAddress
        val condition =
            LimitCondition(
                actionEntity.readingType,
                actionEntity.value,
                actionEntity.conditionType
            )

        val parameters = mapOf(
            Pair(Outcome.TEXT_MESSAGE, actionEntity.textMessage),
            Pair(Outcome.PHONE_NUMBER, actionEntity.phoneNumber)
        )

        val outcome = Outcome(parameters, actionEntity.outcomeType)

        return GeneralAction(id, name, deviceMacAddress, condition, outcome)
    }
}