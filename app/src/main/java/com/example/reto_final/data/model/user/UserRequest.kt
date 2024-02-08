package com.example.reto_final.data.model.user

import android.os.Parcelable
import com.example.reto_final.data.model.userGroup.UserChatInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRequest (
    var id: Int?,
    val name: String,
    val surname: String,
    val email: String,
    val phone_number1: Int,
    val roleId: Int,
    val chatId: List<Int>,
    val userChatInfo: List<UserChatInfo>
): Parcelable