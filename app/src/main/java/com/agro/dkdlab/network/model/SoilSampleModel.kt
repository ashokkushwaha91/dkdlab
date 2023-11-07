package com.agro.dkdlab.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sample")
data class SoilSampleModel (
    @PrimaryKey(autoGenerate = true)
    var ids: Int,
    var id: String?=null,
    val farmerPhoto: String?=null,
    var farmerName: String?=null,
    val fatherName: String?=null,
    val farmerMobileNumber: String?=null,
    val khasraNumber: String?=null,
    val farmSize: String?=null,
    val sampleBarCode: String?=null,
    val gender: String?=null,
    val primaryPhone: String?=null,
    val category: String?=null,
    val cropName: String?=null,
    val irrigationSource: String?=null,
    val latitude: Double?=0.0,
    val longitude: Double?=0.0,
    val creationDate: String?=null,
    val creationTime: String?=null,
    var nrangName: String?=null,
    var prangName: String?=null,
    var krangName: String?=null,
    var ocRangName: String?=null,
    var copperRangName: String?=null,
    var maganeseRangName: String?=null,
    var zincRangName: String?=null,
    var ironRangName: String?=null,
    var boronRangName: String?=null,
    var sulphurRangName: String?=null,
    var phRangName: String?=null,
    val message: String?=null,
    val status: Boolean?=null,
    var stateName: String?=null,
    var districtName: String?=null,
    var blockName: String?=null,
    var villageName: String?=null,
    var pincode: String?=null
)
