package com.example.dormitoryapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.CreateProfileStatus
import com.example.dormitoryapp.utils.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(val app: Application) : AndroidViewModel(app) {

    val createProfileStatus = MutableLiveData<CreateProfileStatus>()
    val updateProfileStatus = MutableLiveData<CreateProfileStatus>()
    val createProfileResponse = MutableLiveData<ResponseModel>()
    val updateProfileResponse = MutableLiveData<ResponseModel>()
    val isLoading = MutableLiveData(false)
    val isWaiting = MutableLiveData(false)
    val profile = MutableLiveData<ProfileModel>()
    val profileById = MutableLiveData<ProfileModel>()
    val profileId = MutableLiveData<Int>()
    val users = MutableLiveData<List<ProfileModel>>()

    @SuppressLint("CheckResult")
    fun createProfile(profile: ProfileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue( true)
                val response = DormitoryClient.retrofit.createProfile(profile)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        createProfileStatus.value = CreateProfileStatus.SUCCESS
                        val body = response.body()!!
                        PrefsManager(app).saveProfile(body.value)
                        isLoading.value = false
                    } else {
                        val body = response.errorBody()?.string() as ResponseModel
                        createProfileResponse.value = body
                        createProfileStatus.value = CreateProfileStatus.FAIL
                        isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getProfileId() {
        profileId.value = PrefsManager(app).getProfile().id
    }

    fun updateProfile(profile: ProfileModel, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue( true)
                val response = DormitoryClient.retrofit.updateProfile(profile, id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        updateProfileStatus.value = CreateProfileStatus.SUCCESS
                        val body = response.body()!!
                        PrefsManager(app).saveProfile(body.value)
                        isLoading.value = false
                    } else {
                        updateProfileStatus.value = CreateProfileStatus.FAIL
                        val body = response.errorBody()?.string() as ResponseModel
                        updateProfileResponse.value = body
                        isLoading.value = false
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isWaiting.postValue( true)
                val response = DormitoryClient.retrofit.getProfileById(idProfile)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        profileById.value = response.body()
                        profile.value = response.body()
                        isWaiting.value = false
                    } else {
                        profile.value = PrefsManager(app).getProfile()
                        isWaiting.value = false
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                profile.postValue(PrefsManager(app).getProfile())
            }
        }
    }

    fun getUserById(idProfile: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = DormitoryClient.retrofit.getProfileById(idProfile)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        profileById.value = response.body()
                        profile.value = response.body()
                    } else {

                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.getAllUsers()
                isLoading.postValue(true)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        users.value = response.body()
                        isLoading.value = false
                    } else {
                        Log.d("no users", response.errorBody()?.string().toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}