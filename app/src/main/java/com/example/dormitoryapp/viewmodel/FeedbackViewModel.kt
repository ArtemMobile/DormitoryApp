package com.example.dormitoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.FeedbackModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.FeedbackStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackViewModel : ViewModel() {

    val feedbackResponse = MutableLiveData<ResponseModel>()
    val isLoading = MutableLiveData<Boolean>()
    val status = MutableLiveData<FeedbackStatus>()

    fun sendFeedBack(feedbackModel: FeedbackModel) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                isLoading.postValue(true)
                val response = DormitoryClient.retrofit.sendFeedback(feedbackModel)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        feedbackResponse.value = response.body()
                        status.value = FeedbackStatus.SUCCESS
                        isLoading.value = false
                    } else {
                        status.value = FeedbackStatus.FAIL
                        Log.d("feedback error:", response.errorBody()?.string().toString())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearStatus(){
        status.value = FeedbackStatus.NOTHING
    }
}