package com.example.reto_final.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.group.GroupDao

@Database(
    entities = [DbGroup::class],
    version = 1,
    exportSchema = false
)
abstract class MyAppRoomDataBase: RoomDatabase() {
    abstract fun groupDao(): GroupDao
}
