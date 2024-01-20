package com.example.reto_final.data.repository.local

import androidx.room.TypeConverter
import java.util.Date

class Convertidores {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}