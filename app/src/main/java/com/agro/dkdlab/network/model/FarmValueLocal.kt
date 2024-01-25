package com.agro.dkdlab.network.model

data class FarmValueLocal(
    val farmId: Int,
    val farmingType: String,
    val nValue: String,
    val pValue: String,
    val kValue: String,
    val organicCarbon: String,
    val phValue: String,
    val farmSize: String?,
    val currentFarmCrop: String?// need for seed calculation
)
