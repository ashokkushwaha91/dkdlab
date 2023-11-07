package com.agro.dkdlab.ui.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.agro.dkdlab.network.model.CropModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.database.entities.ReportEntity
import com.agro.dkdlab.ui.database.entities.TestParametersModel


@Dao
interface ReportDao {
    @Query("SELECT * FROM user")
    fun getSoilSampleRecord(): LiveData<List<ReportEntity>>

    @Query("SELECT * FROM user WHERE sampleBarCode =:sampleBarCode")
    fun getReportByBarcode(sampleBarCode: String): LiveData<ReportEntity>

    @Query("SELECT * FROM user WHERE sampleBarCode LIKE :first LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): ReportEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg users: ReportEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateTestReport(reportData: ReportEntity)

    @Delete
    fun delete(user: ReportEntity)



    ////////////////

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSample(vararg sample: SoilSampleModel)

    @Query("SELECT * FROM sample")
    fun getSoilSampleRecordNew(): LiveData<List<SoilSampleModel>>


    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateTestReportNew(reportData: SoilSampleModel)

    @Query("SELECT * FROM crop")
    fun getCrop(): LiveData<List<CropModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertCrop(cropData: List<CropModel>)
}