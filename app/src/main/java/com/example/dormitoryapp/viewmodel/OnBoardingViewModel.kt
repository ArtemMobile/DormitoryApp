package com.example.dormitoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dormitoryapp.R
import com.example.dormitoryapp.model.dto.OnboardingItemModel
import com.example.dormitoryapp.utils.PrefsManager

class OnBoardingViewModel(val app: Application) : AndroidViewModel(app) {

    var isLastPage = MutableLiveData(false)
    var isOnBoardingPassed = MutableLiveData(false)

    val onBoardingList = listOf(
        OnboardingItemModel(
            "Добро пожаловать в главное приложение общежетия МЦК-КТИТС!",
            "Приложение являет собой агрегатор всех собыйти общежития. Следите за новостями, знакомьтесь с новыми людьми, выстраивайте общение с помошью нашего приложения!",
            R.drawable.ob1
        ),
        OnboardingItemModel(
            "Будьте социально-активными!",
            "Организовывайте встречи, ищите друзей или компанию для совместного провождения досуга",
            R.drawable.ob2
        ),
        OnboardingItemModel(
            "Нужна помощь по учёбе?",
            "Напишите пост с просьбой помочь разобраться в непонятной теме или предмете, вокруг много светлых разумов готовых прийти на помощь!",
            R.drawable.ob3
        )
    )

    fun setIsLastPage(isLast: Boolean) {
        isLastPage.value = isLast
    }

    fun setOnBoardingPassed() {
       PrefsManager(app).setOnboardingPassed()
        isOnBoardingPassed.value = true
    }
}
