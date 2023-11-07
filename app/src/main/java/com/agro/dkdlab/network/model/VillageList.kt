package com.agro.dkdlab.network.model

data class VillageList (
    val message: String,
    val creationDate: Long,
    val statusCode: Int,
    val exception: Any? = null,
    val dataList: List<Village>
)

data class Village (
    val value1: String,
    val value2: String
)
