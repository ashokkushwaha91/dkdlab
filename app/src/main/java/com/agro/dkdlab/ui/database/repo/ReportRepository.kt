package com.agro.dkdlab.ui.database.repo

import androidx.annotation.WorkerThread
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.agro.dkdlab.network.model.CropModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.database.entities.ReportEntity
import com.agro.dkdlab.ui.database.daos.ReportDao
import com.agro.dkdlab.ui.database.entities.TestParametersModel
import javax.inject.Inject

class ReportRepository  @Inject constructor(private val reportDao: ReportDao){
    // Other functions from YourDao.kt
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(stock: ReportEntity) =reportDao.insertAll(stock)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTestReport(reportData: ReportEntity){
        reportDao.updateTestReport(reportData)
    }


     fun getSoilSampleRecord() = reportDao.getSoilSampleRecord()
     fun getReportByBarcode(sampleBarCode:String) = reportDao.getReportByBarcode(sampleBarCode)


    /////////////////

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertNew(sampleData: SoilSampleModel) =reportDao.insertSample(sampleData)

    fun getSoilSampleRecordNew() = reportDao.getSoilSampleRecordNew()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTestReportNew(reportData: SoilSampleModel){
        reportDao.updateTestReportNew(reportData)
    }

    fun getCrop() = reportDao.getCrop()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCrop(cropData: List<CropModel>) =reportDao.insertCrop(cropData)

}