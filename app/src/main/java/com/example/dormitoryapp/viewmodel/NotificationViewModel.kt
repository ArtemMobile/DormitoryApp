package com.example.dormitoryapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.utils.SendNotificationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(val app: Application) : AndroidViewModel(app) {

    val sendResponse = MutableLiveData<ResponseModel>()
    val sendNotificationStatus = MutableLiveData<SendNotificationStatus>()
    val isLoading = MutableLiveData(false)

    fun sendNotifications(idUser: Int, idPost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.setValue(true)
                val notifier = PrefsManager(app).getProfile()
                val response =
                    DormitoryClient.retrofit.sendNotification(notifier.id, idUser, idPost)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        sendResponse.value = response.body()
                        sendNotificationStatus.value = SendNotificationStatus.SUCCESS
                        isLoading.value = false
                    } else {
                        sendNotificationStatus.value = SendNotificationStatus.FAILURE
                        isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.d("NotificationViewModel", it) }
            }
        }
    }

    fun clearStatus() {
        sendNotificationStatus.value = SendNotificationStatus.NOTHING
    }
}