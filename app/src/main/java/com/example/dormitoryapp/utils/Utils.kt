package com.example.dormitoryapp.utils

object Utils {
    fun isEmailValid(email: String): Boolean {
        val pattern = "[a-z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-z0-9][a-z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-z0-9][a-z0-9\\-]{0,25}" +
                ")+"
        val regex = Regex(pattern)
        return regex.matches(email)
    }
}