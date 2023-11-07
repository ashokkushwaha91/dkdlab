package com.agro.dkdlab.ui.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.agro.dkdlab.network.model.CropModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.database.daos.ReportDao
import com.agro.dkdlab.ui.database.entities.Converters
import com.agro.dkdlab.ui.database.entities.ReportEntity

@Database(entities = [ReportEntity::class, SoilSampleModel::class, CropModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): ReportDao
}