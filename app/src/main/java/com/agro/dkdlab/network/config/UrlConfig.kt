package com.agro.dkdlab.network.config

object UrlConfig {
    const val PRODUCTION = "_production"
    const val TEST = "_test"
    private const val TEST_DOMAIN = "http://182.18.155.165:8080"
    private const val PROD_DOMAIN = "http://182.18.155.172:8080"
    var BASE_URL = ""
    var IMAGE_URL = ""
    fun get(type: String): UrlConfig{

        val postFix = "/haritkranti-0.0.1-SNAPSHOT/api/"
        val isProd = type == PRODUCTION
        BASE_URL = if (isProd) "$PROD_DOMAIN$postFix" else "$TEST_DOMAIN$postFix"
        IMAGE_URL = if (isProd) PROD_DOMAIN else TEST_DOMAIN
        return this
    }

}