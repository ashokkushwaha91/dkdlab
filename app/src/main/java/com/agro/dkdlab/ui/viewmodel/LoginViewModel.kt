package com.agro.dkdlab.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.agro.dkdlab.network.base.ApiEndPoint
import com.agro.dkdlab.network.config.ApiRequest
import com.agro.dkdlab.network.tool.NoConnectivityException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import retrofit2.http.Body
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiService: ApiEndPoint): ViewModel() {
    fun sendOtp(mobileNumber:String,languageName:String) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.sendOtp(mobileNumber,languageName)
            emit(ApiRequest.success(data = response))
            /*if (response.isSuccessful && response.body() != null){
                emit(ApiRequest.success(data = response.body()))
            }else{
                emit(ApiRequest.error(data = null, message = response.message()))
            }*/
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
    fun verifyDKDOtp(@Body body: HashMap<String, String>) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.verifyOTP(body)
            emit(ApiRequest.success(data = response))
            /*if (response.isSuccessful && response.body() != null){
                emit(ApiRequest.success(data = response.body()))
            }else{
                emit(ApiRequest.error(data = null, message = response.message()))
            }*/
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }

    fun registerUser(@Body body: Map<String, Any>) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.createUser(body)
            emit(ApiRequest.success(data = response))
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
    fun registerUserAddress(@Body body: Map<String, String>) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.createUserAddress(body)
            emit(ApiRequest.success(data = response))
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
    fun findUserAddressByUserId(userId :String) = liveData(Dispatchers.IO) {
        emit(ApiRequest.loading(data = null))
        try {
            val response = apiService.findUserAddressByUserId(userId)
            emit(ApiRequest.success(data = response))
        }catch (exception: Exception){
            val type = if (exception is NoConnectivityException) "internet" else exception.message.toString()
            emit(ApiRequest.error(data = null, message = type))
        }
    }
}