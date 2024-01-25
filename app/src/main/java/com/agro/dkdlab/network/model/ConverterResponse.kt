package com.agro.dkdlab.network.model

data class ConverterResponse (
    val areaToBeConverted: String,
    val unit: String,
    val state: String,
    val convertedArea: String,
    val remarks: String
)
