package com.agro.dkdlab.locale

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.*

class LocaleManager {

    companion object {

        fun init() {
            localeManager = LocaleManager()
        }
        private lateinit var localeManager: LocaleManager
        fun get(): LocaleManager = localeManager
    }

    fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
        var context = c
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else {
            configuration.locale = localeToSwitchTo
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context = context.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        return ContextWrapper(context)
    }

     fun updateResources(context: Context, language: String): Context {
        var context = context

        val locale = Locale(language)
        Locale.setDefault(locale)

        val res: Resources = context.resources
        val config = Configuration(res.configuration)

        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }

        return context
    }

    fun setLocale(context: Context, lang: String?) {
        Log.i("setLocale", "setLocale: =" + lang)
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val res = context.resources
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, res.displayMetrics)

    }

}