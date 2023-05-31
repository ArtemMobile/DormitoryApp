package com.example.dormitoryapp.app

import android.app.Application
import com.example.dormitoryapp.utils.PrefsManager

class DormApp : Application() {

    companion object{
        lateinit var prefsManager: PrefsManager
    }

    override fun onCreate() {
        super.onCreate()
        prefsManager = PrefsManager(baseContext)
    }
}