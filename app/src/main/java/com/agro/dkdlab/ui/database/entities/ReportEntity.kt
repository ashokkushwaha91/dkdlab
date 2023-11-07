package com.agro.dkdlab.ui.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "user")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var mobile: String,
    var sampleBarCode: String,
    var farmerName: String,
    var farmerMobile: String,
    var date: String,
    @TypeConverters(Converters::class)
    var testParametersModel: List<TestParametersModel>,
)

