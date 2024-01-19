package com.example.reto_final.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.group.GroupDao
import com.example.reto_final.data.repository.local.message.DbMessage
import com.example.reto_final.data.repository.local.message.MessageDao
import com.example.reto_final.data.repository.local.user.DbUser
import com.example.reto_final.data.repository.local.group.DbUserGroup
import com.example.reto_final.data.repository.local.user.UserDao

@Database(
    entities = [DbGroup::class, DbMessage::class, DbUser::class, DbUserGroup::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class MyAppRoomDataBase: RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
}
