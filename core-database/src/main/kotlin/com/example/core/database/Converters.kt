package com.example.core.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toIntList(json: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, listType)
    }
}