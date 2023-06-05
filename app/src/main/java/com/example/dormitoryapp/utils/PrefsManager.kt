package com.example.dormitoryapp.utils

import android.content.Context
import com.example.dormitoryapp.model.dto.ProfileModel
import com.google.gson.Gson

class PrefsManager(context: Context) {

    private val prefs = context.getSharedPreferences("dormitoryApp", Context.MODE_PRIVATE)

    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
    }

    fun getEmail(): String {
        return prefs.getString("email", "") ?: ""
    }

    fun setOnboardingPassed() {
        prefs.edit().putBoolean("onBoardingPassed", true).apply()
    }

    fun setLoginPassed() {
        prefs.edit().putBoolean("loginPassed", true).apply()
    }

    fun setCreatePasswordPassed() {
        prefs.edit().putBoolean("createPasswordPassed", true).apply()
    }

    fun getOnboardingPassed(): Boolean {
        return prefs.getBoolean("onBoardingPassed", false)
    }

    fun getLoginPassed(): Boolean {
        return prefs.getBoolean("loginPassed", false)
    }


    fun getCreatePasswordPassed(): Boolean {
        return prefs.getBoolean("createPasswordPassed", false)
    }

    fun getCreateProfilePassed(): Boolean {
        return prefs.getString("profile", null) != null
    }

    fun saveProfile(profileModel: ProfileModel) {
        prefs.edit().putString("profile", Gson().toJson(profileModel)).apply()
    }

    fun getProfile(): ProfileModel {
        return Gson().fromJson(prefs.getString("profile", "{ }"), ProfileModel::class.java)
    }

    fun savePhoto(photo: String){
        prefs.edit().putString("avatar", photo).apply()
    }

    fun getPhoto(): String? {
        return prefs.getString("avatar", "")
    }
}