package com.example.dormitoryapp.model.dto

data class PostModel(
    val description: String,
    val id: Int,
    val idProfile: Int,
    val idType: Int,
    val isPayable: Boolean,
    var name: String,
    val notificationDate: Any,
    val publishDate: String,
    val title: String,
    val firstName: String,
    val surname: String
)