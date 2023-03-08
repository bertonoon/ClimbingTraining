package com.example.climbingtraining.db

import androidx.room.TypeConverter
import com.example.climbingtraining.models.SingleHangboard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    private var gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromSingleHangboard(hangboardType: SingleHangboard?): String? {
        return gson.toJson(hangboardType)
    }

    @TypeConverter
    fun toSingleHangboard(data: String?): SingleHangboard? {
        val type = object : TypeToken<SingleHangboard>() {}.type
        return gson.fromJson(data, type)
    }


}