package com.agro.dkdlab.network.model

data class SampleCount (
    val message: String,
    val statusCode: Int,
    val dataList: List<Long>
)
