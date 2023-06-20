package com.example.dormitoryapp.model.dto

data class PostModel(
    val description: String,
    val id: Int,
    val idProfile: Int,
    val idType: Int,
    val isPayable: Boolean,
    var name: String,
    val notificationDate: String,
    val publishDate: String,
    val title: String,
    val firstName: String,
    val surname: String,
    val expireDate: String
) : java.io.Serializable{
    override fun hashCode(): Int {
        return super.hashCode()
    }
}