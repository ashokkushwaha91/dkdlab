package com.agro.dkdlab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.agro.dkdlab.network.base.ApiEndPoint
import com.agro.dkdlab.network.config.ApiRequest
import com.agro.dkdlab.network.tool.NoConnectivityException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(private val apiService: ApiEndPoint) : ViewModel() {
    fun getStates() = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.getStates()
            emit(ApiRequest.success(data = response))
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }

    fun getDistricts(stateId:String) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.getDistricts(stateId)
            emit(ApiRequest.success(data = response))
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
    fun getBlock(districtId:String) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.getBlock(districtId)
            emit(ApiRequest.success(data = response))
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
}