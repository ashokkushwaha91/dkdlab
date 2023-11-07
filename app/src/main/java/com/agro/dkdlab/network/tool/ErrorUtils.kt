package com.agro.dkdlab.network.tool

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response


class ErrorUtils {
    companion object ErrorParser{
        fun parseError(response: Response<*>): APIError {
            var errorBody = ""
            var error = APIError(0, "")
            try {
                errorBody = response.errorBody()?.string().toString()
                Log.e("ErrorParser", errorBody)
                if (errorBody.isNullOrEmpty()){
                    error.message = response.message()
                    error.status = response.code()
                }else{
                    val er = Gson().fromJson(errorBody, APIError::class.java)
                    error.status = response.code()
                    error.message = if(er.message.isNullOrEmpty()) errorBody else er.message
                }
            }catch (e: Exception){
                error.message = errorBody
                error.status = response.code()
            }

            return error
        }
    }
}