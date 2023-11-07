package com.agro.dkdlab.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop")
data class CropModel (
    @PrimaryKey(autoGenerate = true)
    var ids: Int?=0,
    val id:String,
    val cropName:String
)