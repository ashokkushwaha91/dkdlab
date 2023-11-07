package com.agro.dkdlab.network.model

import com.google.gson.annotations.SerializedName

data class NPKAverageValue (
    val message: String,
    val statusCode: Int,
    val dataList: List<DataList>
)

data class DataList (
    val ocRangName: RangName,
    val phRangName: PhRangName,
    val nrangName: RangName,
    val prangName: RangName,
    val krangName: RangName
)

data class RangName (
    val High: Int,
    @SerializedName("Very Low")
    val veryLow: Int,
    val Low: Int,
    @SerializedName("Very High")
    val veryHigh: Int,
    val Medium: Int
)

data class PhRangName (
    val Neutral: Int,
    @SerializedName("Strongly Acidic")
    val stronglyAcidic: Int,
    @SerializedName("Moderately Acidic")
    val moderatelyAcidic: Int,
    @SerializedName("Acid Sulphate")
    val acidSulphate: Int,
    @SerializedName("Slightly Acidic")
    val slightlyAcidic: Int,
    @SerializedName("Highly Acidic")
    val highlyAcidic: Int,
    @SerializedName("Moderately Alkaline")
    val moderatelyAlkaline: Int,
    @SerializedName("Strongly Alkaline")
    val stronglyAlkaline: Int
)
