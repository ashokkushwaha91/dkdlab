package com.agro.dkdlab.network.model

import com.google.gson.annotations.SerializedName

data class FertCalcManualModel (
    val id: Any? = null,
    val soilHealthCardNumber: String,
    val ocRangeValue: String,
    val phRangeValue: String,
    val ocRangeName: String,
    val phRangeName: String,
    val organic: FarmingType,
    val inOrganic: FarmingType,
    val mix25: FarmingType,
    val mix50: FarmingType,
    val mix75: FarmingType,
    val nrangeValue: String,
    val prangeValue: String,
    val krangeValue: String,
    val nrangeName: String,
    val prangeName: String,
    val krangeName: String
)

/*
data class InOrganic (
    val Potato: InOrganicCropValue,
    val Gram: InOrganicCropValue,
    val Mustard: InOrganicCropValue,
    val Paddy: InOrganicCropValue,
    val Wheat: InOrganicCropValue,
    val Sugarcane: InOrganicCropValue
)

data class InOrganicCropValue (
    val ssp: Long,
    val dap: Long,
    val urea: Long,
    val mop: Long
)
*/

data class FarmingType (
    val Potato: MixCropValue,
    val Gram: MixCropValue,
    val Mustard: MixCropValue,
    val Paddy: MixCropValue,
    val Wheat: MixCropValue,
    val Sugarcane: MixCropValue,
    val Cotton: MixCropValue,
    @SerializedName(value="CottonHybrid1", alternate= ["Cotton Hybrid", "CottonHybrid"])
    val CottonHybrid: MixCropValue
)

data class MixCropValue (
    val kmb: Double,
    val ssp: Double,
    val dap: Double,
    val fym: Double,
    val rp: Double,
    val psb: Double,
    val urea: Double,
    val mop: Double,
    val sentences: List<String>? = null
)
