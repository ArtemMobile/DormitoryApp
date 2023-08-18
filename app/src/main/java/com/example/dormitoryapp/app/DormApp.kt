package com.example.dormitoryapp.app

import android.app.Application
import android.widget.Toast
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.utils.PrefsManager

class DormApp : Application() {

    companion object{
        lateinit var prefsManager: PrefsManager
        var currentUser: ProfileModel? = null
    }


    override fun onCreate() {
        super.onCreate()
        currentUser = PrefsManager(baseContext).getProfile()
        prefsManager = PrefsManager(baseContext)
    }
}