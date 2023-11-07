package com.agro.dkdlab.ui.database.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {
    @TypeConverter // note this annotation
    fun fromTestValuesList(optionValues: List<TestParametersModel>?): String? {
        if (optionValues == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<TestParametersModel>>() {}.type
        return gson.toJson(optionValues, type)
    }

    @TypeConverter // note this annotation
    fun toTestValuesList(optionValuesString: String?): List<TestParametersModel>? {
        if (optionValuesString == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<TestParametersModel>>() {}.type
        return gson.fromJson(optionValuesString, type)
    }
}