package com.agro.dkdlab.network.tool

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response



class ErrorInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain!!.proceed(chain.request())
        try {
            Log.e("REQUEST_URL", "${originalResponse.request.url}")
            Log.e("RESPONSE_CODE", "${originalResponse.code}")
        }catch (e: Exception){}
        return originalResponse
    }

}