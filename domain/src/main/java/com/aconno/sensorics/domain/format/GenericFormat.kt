package com.aconno.sensorics.domain.format

data class GenericFormat(
    private val formatName: String,
    private val icon: String,
    private val format: List<ByteFormat>,
    private val requiredFormat: List<ByteFormatRequired>
) : AdvertisementFormat {

    override fun getName(): String {
        return formatName
    }

    override fun getIcon(): String {
        return icon
    }

    override fun getFormat(): Map<String, ByteFormat> {
        val map = hashMapOf<String, ByteFormat>()

        format.forEach {
            map[it.name] = it
        }

        return map
    }

    override fun getRequiredFormat(): List<ByteFormatRequired> {
        return requiredFormat
    }
}