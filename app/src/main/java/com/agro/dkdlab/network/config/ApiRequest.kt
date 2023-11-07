package com.agro.dkdlab.network.config


data class ApiRequest<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): ApiRequest<T> = ApiRequest(status = Status.SUCCESS, data = data, message = null)
        fun <T> error(data: T?, message: String): ApiRequest<T> = ApiRequest(status = Status.ERROR, data = data, message = message)
        fun <T> loading(data: T?): ApiRequest<T> = ApiRequest(status = Status.LOADING, data = data, message = null)
    }

}