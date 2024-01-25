package com.agro.dkdlab.network.model

data class FarmDetailsModel(
    val farmId: Int?,
    val correctCrop: Boolean,
    val sowingDate: String?,
    var farmingType: String?,
    val nValue: String?,
    val pValue: String?,
    val kValue: String?,
    val organicCarbon: String?,
    val phValue: String?,
    val soilMoisture: String?,
    val nColor: String?,
    val pColor: String?,
    val kColor: String?,
    val ocColor: String?,
    val nLevel: String?,
    val pLevel: String?,
    val kLevel: String?,
    val ocLevel: String?,
    val cropKharif2019: String?,
    val cropRabi201920: String?,
    val cropKharif2020: String?,
    val currentFarmCrop: String?,
    val farmCordinateList: List<Farm>,
    var pos: Int = 0
)

data class Farm(
    val farmId: Int?,
    val lat: Double,
    val lon: Double,
)
