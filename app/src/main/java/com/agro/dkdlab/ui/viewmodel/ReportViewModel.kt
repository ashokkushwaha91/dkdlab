package com.agro.dkdlab.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agro.dkdlab.network.model.CropModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.database.entities.ReportEntity
import com.agro.dkdlab.ui.database.entities.TestParametersModel
import com.agro.dkdlab.ui.database.repo.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository, application: Application): AndroidViewModel(application) {

    fun insert(userData: ReportEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(userData)
    }
    fun updateTestReport(reportData:ReportEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTestReport(reportData)
    }

    fun getSoilSampleRecord() = repository.getSoilSampleRecord()

    fun getReportByBarcode(sampleBarCode:String) = repository.getReportByBarcode(sampleBarCode)


    /////////////////

    fun insertNew(sampleData: SoilSampleModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNew(sampleData)
    }
    fun updateTestReportNew(reportData:SoilSampleModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTestReportNew(reportData)
    }

    fun getSoilSampleRecordNew() = repository.getSoilSampleRecordNew()

    fun getReportByBarcodeNew(barcode:String) = repository.getReportByBarcode(barcode)

    fun getCrop() = repository.getCrop()

    fun insertCrop(cropData: List<CropModel>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCrop(cropData)
    }

}