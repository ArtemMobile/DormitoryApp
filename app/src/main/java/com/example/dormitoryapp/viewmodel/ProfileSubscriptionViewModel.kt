package com.example.dormitoryapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.*
import com.example.dormitoryapp.utils.PostSubscriptionStatus
import com.example.dormitoryapp.utils.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileSubscriptionViewModel(val app: Application) : AndroidViewModel(app) {

    val profileSubscriptionOfUser = MutableLiveData<List<ProfileSubscriptionModel>>()
    val addPostSubscriptionResponse = MutableLiveData<ResponseModel>()
    val status = MutableLiveData<PostSubscriptionStatus>()

    fun getProfileSubscriptions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.getProfileSubscriptionsByProfile(PrefsManager(app).getProfile().id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        profileSubscriptionOfUser.value = response.body()
                        status.value = PostSubscriptionStatus.SUCCESS
                    } else {
                        status.value = PostSubscriptionStatus.SUCCESS
                    }
                }

            } catch (e: Exception) {
                Log.d("getProfileSubscriptions error", e.message.toString())
            }
        }
    }

    fun addProfileSubscription(idPost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sub = CreateProfileSubscriptionModel(PrefsManager(app).getProfile().id, idPost, true)
                val response = DormitoryClient.retrofit.addProfileSubscription(sub)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        addPostSubscriptionResponse.value = response.body()
                        getProfileSubscriptions()
                    } else {

                    }
                }
            } catch (e: Exception) {
                Log.d("addPostSubscription error", e.message.toString())
            }
        }
    }

    fun deleteProfileSubscription(idPost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sub = CreateProfileSubscriptionModel(PrefsManager(app).getProfile().id, idPost, true)
                val response = DormitoryClient.retrofit.deleteProfileSubscription(sub)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        addPostSubscriptionResponse.value = response.body()
                        getProfileSubscriptions()
                    } else {

                    }
                }
            } catch (e: Exception) {
                Log.d("deletePostSubscription error", e.message.toString())
            }
        }
    }
}