package ru.pkstudio.localhomeworkandtaskmanager.main.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverters {
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}