package com.agro.dkdlab.network.model

import com.google.gson.annotations.SerializedName

data class FertCalcModel(
    val MIX25 : FertCalcManualModelNew,
    val MIX50 : FertCalcManualModelNew,
    val MIX75 : FertCalcManualModelNew,
    val ORGANIC : FertCalcManualModelNew,
    @SerializedName("MOP/SSP/Urea") val MOP_SSP_Urea : FertCalcManualModelNew,
    @SerializedName("DAP/MOP/Urea") val DAP_MOP_Urea : FertCalcManualModelNew,
    @SerializedName("102626/DAP/Urea") val _102626_DAP_Urea : FertCalcManualModelNew,
    @SerializedName( "123216/DAP/Urea") val _123216_DAP_Urea : FertCalcManualModelNew,
)
