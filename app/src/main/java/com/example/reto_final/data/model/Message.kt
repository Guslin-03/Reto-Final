package com.example.reto_final.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message (
    var id: Int?,
    val text: String,
    val sentDate: Date,
    val saveDate: Date,
    val groupId: Int,
    val authorId: Int,
) : Parcelable