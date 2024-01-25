package com.agro.dkdlab.network.model

data class FertCropModel (
    val id:String,
    val cropName:String?,
    var seed:Boolean?,
    val stateCode:String?
)