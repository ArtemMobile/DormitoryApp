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
    val deletePostSubscriptionResponse = MutableLiveData<ResponseModel>()
    val addStatus = MutableLiveData<PostSubscriptionStatus>()
    val deleteStatus = MutableLiveData<PostSubscriptionStatus>()

    fun getProfileSubscriptions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.getProfileSubscriptionsByProfile(PrefsManager(app).getProfile().id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        profileSubscriptionOfUser.value = response.body()
                    } else {
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
                        addStatus.value = PostSubscriptionStatus.SUCCESS
                        getProfileSubscriptions()
                    } else {
                        addStatus.value = PostSubscriptionStatus.FAILURE
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
                        deletePostSubscriptionResponse.value = response.body()
                        deleteStatus.value = PostSubscriptionStatus.SUCCESS
                        getProfileSubscriptions()
                    } else {
                        deleteStatus.value = PostSubscriptionStatus.FAILURE
                    }
                }
            } catch (e: Exception) {
                Log.d("deletePostSubscription error", e.message.toString())
            }
        }
    }

    fun clearStatus(){
        addStatus.value = PostSubscriptionStatus.NOTHING
        deleteStatus.value = PostSubscriptionStatus.NOTHING
    }
}