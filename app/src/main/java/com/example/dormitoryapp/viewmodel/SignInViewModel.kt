package com.example.dormitoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.LoginModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.model.dto.SignInModel
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.utils.SignInStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(val app: Application) : AndroidViewModel(app) {

    var email = MutableLiveData<String>()
    init {
        email.value = PrefsManager(app).getEmail()
    }
    var signInStatus = MutableLiveData<SignInStatus>()
    var responseMessage = MutableLiveData<ResponseModel>()

    fun signIn(signInModel: SignInModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = DormitoryClient.retrofit.signIn(signInModel)
            withContext(Dispatchers.Main) {
                responseMessage.value = response.body()
                if (response.isSuccessful) {
                    signInStatus.value = SignInStatus.SUCCESS
                } else {
                    signInStatus.value = SignInStatus.FAIL
                }
            }
        }
    }

    fun sendCode() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = DormitoryClient.retrofit.sendCode(LoginModel(email.value!!))
            withContext(Dispatchers.Main) {
                responseMessage.value = response.body()
            }
        }
    }

    fun clearSignInStatus() {
        viewModelScope.launch {
            signInStatus.value = SignInStatus.NOTHING
            PrefsManager(app).setLoginPassed()
        }
    }
}