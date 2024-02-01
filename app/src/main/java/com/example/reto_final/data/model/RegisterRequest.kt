package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
    @Parcelize
    data class RegisterRequest(

        val id: Int,
        val DNI: String,
        val name: String,
        val surname: String,
        val phoneNumber1: Int,
        val phoneNumber2: Int,
        val address: String,
        val photo: String,
        val email: String,
        val oldPassword: String,
        val newPassword: String

        ): Parcelable {
        constructor(
            DNI: String,
            name: String,
            surname: String,
            phoneNumber1: Int,
            phoneNumber2: Int,
            address: String,
            photo: String,
            email: String,
            oldPassword: String,
            newPassword: String
        ) : this(
            id = 0,
            DNI = DNI,
            name = name,
            surname = surname,
            phoneNumber1 = phoneNumber1,
            phoneNumber2 = phoneNumber2,
            address = address,
            photo = photo,
            email = email,
            oldPassword = oldPassword,
            newPassword = newPassword
        )


    }