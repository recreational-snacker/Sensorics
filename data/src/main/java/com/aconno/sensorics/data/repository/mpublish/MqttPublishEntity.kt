package com.aconno.sensorics.data.repository.mpublish

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "mqtt_publish")
data class MqttPublishEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var url: String,
    var clientId: String,
    var username: String,
    var password: String,
    var topic: String,
    var qos: Int,
    var enabled: Boolean,
    var timeType: String,
    var timeMillis: Long,
    var lastTimeMillis: Long,
    var dataString: String
)