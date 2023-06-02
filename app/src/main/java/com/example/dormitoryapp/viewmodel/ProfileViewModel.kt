package com.example.dormitoryapp.viewmodel

import android.annotation.SuppressLint
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
    val updateProfileStatus = MutableLiveData<CreateProfileStatus>()
    val createProfileResponse = MutableLiveData<ResponseModel>()
    val updateProfileResponse = MutableLiveData<ResponseModel>()
    val isLoading = MutableLiveData(false)
    val profile = MutableLiveData<ProfileModel>()
    val profileById = MutableLiveData<ProfileModel>()

    @SuppressLint("CheckResult")
    fun createProfile(profile: ProfileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.createProfile(profile)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        createProfileStatus.value = CreateProfileStatus.SUCCESS
                        val body = response.body()!!
                        PrefsManager(app).saveProfile(body.value)
                    } else {
                        val body = response.errorBody()?.string() as ResponseModel
                        createProfileResponse.value = body
                        createProfileStatus.value = CreateProfileStatus.FAIL
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getProfile() {
        profile.value = PrefsManager(app).getProfile()
    }

    fun updateProfile(profile: ProfileModel, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.updateProfile(profile, id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        updateProfileStatus.value = CreateProfileStatus.SUCCESS
                        val body = response.body()!!
                        PrefsManager(app).saveProfile(body.value)
                    } else {
                        updateProfileStatus.value = CreateProfileStatus.FAIL
                        val body = response.errorBody()?.string() as ResponseModel
                        updateProfileResponse.value = body
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    fun clearStatus() {
        updateProfileStatus.value = CreateProfileStatus.NOTHING
        createProfileStatus.value = CreateProfileStatus.NOTHING
    }

    fun getProfileById(idProfile: Int) {
        try {
            viewModelScope.launch {
                val response = DormitoryClient.retrofit.getProfileById(idProfile)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        profileById.value = response.body()
                    } else {

                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}