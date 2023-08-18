package com.example.dormitoryapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dormitoryapp.model.dto.NewsModel

class NewsViewModel: ViewModel() {

    val news = MutableLiveData<List<NewsModel>>()

    private val newsList = listOf(
        NewsModel(
            "Генеральная уборка",
        "В понедельник 05.06.2023 состоится генеральная уборка. Мы ждём от вас вовлеченности в процесс и чистоты во время проверки во вторник ^_^",
        ),
        NewsModel(
            "Нужна ваша помощь!",
            "Нужно семь совершеннолетних студентов, желающих завтра поехать в лагерь\"Юность\" для уборки, соответственно за баллы. Проезд и питание будут оплачены, также будет дано осовобждение от пар на время пребывания. ",
        ),
        NewsModel(
            "Требутся волонтёры!",
            "На время проведения регионального этапа чемпионата \"Профессионалы\" в техникум требуется 10 студентов",
        ),
    )

    fun getNews(){
        news.value = newsList
    }
}