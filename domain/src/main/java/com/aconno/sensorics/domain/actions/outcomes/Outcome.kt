package com.aconno.sensorics.domain.actions.outcomes

class Outcome(val parameters: Map<String, String>, val type: Int) {

    override fun toString(): String {
        return when (type) {
            0 -> "Notification"
            1 -> "SMS"
            2 -> "Text to Speech"
            3 -> "Vibration"
            else -> throw IllegalArgumentException("Outcome type not valid: $type")
        }
    }

    companion object {

        const val OUTCOME_TYPE_NOTIFICATION = 0
        const val OUTCOME_TYPE_SMS = 1
        const val OUTCOME_TYPE_TEXT_TO_SPEECH = 2
        const val OUTCOME_TYPE_VIBRATION = 3

        const val TEXT_MESSAGE = "textMessage"
        const val PHONE_NUMBER = "phoneNumber"

        fun typeFromString(type: String): Int {
            return when (type) {
                "Notification" -> 0
                "SMS" -> 1
                "Text to Speech" -> 2
                "Vibration" -> 3
                else -> throw IllegalArgumentException("Invalid outcome type: $type")
            }
        }

        fun typeFromInt(type: Int): String {
            return when (type) {
                0 -> "Notification"
                1 -> "SMS"
                2 -> "Text to Speech"
                3 -> "Vibration"
                else -> throw IllegalArgumentException("Invalid outcome type: $type")
            }
        }
    }
}