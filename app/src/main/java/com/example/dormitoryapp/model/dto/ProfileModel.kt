package com.example.dormitoryapp.model.dto

data class ProfileModel(
    val avatar: String,
    val contactInfo: String,
    val groupNumber: Int,
    val interests: String,
    val name: String,
    val patronymic: String,
    val room: Int,
    val surname: String,
    val telegramNick: String,
    val id: Int
)