package com.example.dormitoryapp.model.dto

data class CreateProfileSubscriptionModel(
    val idSubsriber: Int,
    val idProfile: Int,
    val isSubsribed: Boolean
)