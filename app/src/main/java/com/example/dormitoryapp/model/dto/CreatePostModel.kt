package com.example.dormitoryapp.model.dto

data class CreatePostModel(
    val title: String,
    val description: String,
    val idType: Int,
    val idProfile: Int,
    val notificationDate: String,
    val isPayable: Boolean,
    val expireDate: String
)