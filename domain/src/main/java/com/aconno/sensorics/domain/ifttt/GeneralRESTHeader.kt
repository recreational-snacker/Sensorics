package com.aconno.sensorics.domain.ifttt

class GeneralRESTHeader(
    override val id: Long,
    override val rId: Long,
    override val key: String,
    override val value: String
) : RESTHeader