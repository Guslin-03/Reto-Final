package com.example.reto_final.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUser(

    val id: Int,
    var DNI: String,
    var name: String,
    var surname: String,
    var phone_number1: Int,
    var phone_number2: Int,
    var address: String,
    val photo: String,
    val FCTDUAL: Int,
    val email: String,
    val degrees: Array<Degree>,
    val roles: Array<Rol>,
    val department_id: Int,
    var accessToken: String,
    val token: String

): Parcelable {
    constructor(
        DNI: String,
        name: String,
        surname: String,
        phone_number1: Int,
        phone_number2: Int,
        address: String,
        photo: String,
        FCTDUAL: Int,
        email: String,
        department_id: Int
    ) : this(
        id = 1,
        DNI = DNI,
        name = name,
        surname = surname,
        phone_number1 = phone_number1,
        phone_number2 = phone_number2,
        address = address,
        photo = photo,
        FCTDUAL = FCTDUAL,
        email = email,
        degrees = emptyArray(),
        roles = emptyArray(),
        department_id = department_id,
        accessToken = "",
        token=""
    )

}