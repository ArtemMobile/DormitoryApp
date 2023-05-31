package com.example.dormitoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.CreateProfileStatus
import com.example.dormitoryapp.utils.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(val app: Application) : AndroidViewModel(app) {

    val createProfileStatus = MutableLiveData<CreateProfileStatus>()
    val responseMessage = MutableLiveData<ResponseModel>()
    val isLoading = MutableLiveData(false)

    fun createProfile(profile: ProfileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = DormitoryClient.retrofit.createProfile(profile)
            withContext(Dispatchers.Main) {
                responseMessage.value = response.body()
                if (response.isSuccessful) {
                    createProfileStatus.value = CreateProfileStatus.SUCCESS
                    PrefsManager(app).saveProfile(profile)
                } else {
                    createProfileStatus.value = CreateProfileStatus.FAIL
                }
            }
        }
    }
}