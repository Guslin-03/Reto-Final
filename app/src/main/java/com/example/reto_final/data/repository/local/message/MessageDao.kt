package com.example.reto_final.data.repository.local.message

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY id ASC")
    suspend fun getMessages(): List<DbMessage>

}