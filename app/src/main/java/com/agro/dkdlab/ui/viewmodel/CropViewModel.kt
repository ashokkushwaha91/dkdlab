package com.agro.dkdlab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.agro.dkdlab.network.base.ApiEndPoint
import com.agro.dkdlab.network.config.ApiRequest
import com.agro.dkdlab.network.tool.NoConnectivityException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(private val apiService: ApiEndPoint): ViewModel() {
    fun getCropMasterList(stateCode:String) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.getCropMasterList(stateCode)
            emit(ApiRequest.success(data = response))

        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
}