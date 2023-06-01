package com.example.dormitoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.LoginModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.utils.SendCodeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(val app: Application) : AndroidViewModel(app) {

    var sendCodeStatus = MutableLiveData<SendCodeStatus>()
    var responseMessage = MutableLiveData<ResponseModel>()
    var isLoading = MutableLiveData(false)

    fun sendCode(loginModel: LoginModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = DormitoryClient.retrofit.sendCode(loginModel)
                withContext(Dispatchers.Main) {
                    responseMessage.value = response.body()
                    if (response.isSuccessful) {
                        sendCodeStatus.value = SendCodeStatus.SUCCESS
                        isLoading.value = false
                        PrefsManager(app).saveEmail(loginModel.emailAddress)
                    } else
                        sendCodeStatus.value = SendCodeStatus.FAIL
                }
            }
            catch (e: Exception){

            }
        }
    }

    fun clearSendCodeStatus() {
        viewModelScope.launch {
            sendCodeStatus.value = SendCodeStatus.NOTHING
        }
    }
}