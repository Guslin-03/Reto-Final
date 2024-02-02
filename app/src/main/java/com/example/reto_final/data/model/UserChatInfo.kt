package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserChatInfo (
    val userId: Int,
    val chatId: Int,
    val joined: Long,
    val deleted: Long?
) : Parcelable