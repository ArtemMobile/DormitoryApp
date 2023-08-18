package com.example.dormitoryapp.model.dto

data class ResponseModel(
    val contentType: Any,
    val serializerSettings: Any,
    val statusCode: Any,
    var value: Value
)