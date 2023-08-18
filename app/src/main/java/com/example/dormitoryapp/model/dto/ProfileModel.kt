package com.example.dormitoryapp.model.dto

import java.io.Serializable

data class ProfileModel(
    val avatar: String,
    val contactInfo: String,
    val groupNumber: Int,
    val interests: String,
    val firstName: String,
    val patronymic: String,
    val room: Int,
    val surname: String,
    val telegramNick: String,
    val id: Int,
    val deviceId: String,
    val isLogged: Boolean
) : Serializable {
    override fun hashCode(): Int {
        return super.hashCode()
    }
}