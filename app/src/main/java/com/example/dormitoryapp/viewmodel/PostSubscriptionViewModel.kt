package com.example.dormitoryapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.CreatePostSubscriptionModel
import com.example.dormitoryapp.model.dto.PostSubscriptionModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.PostSubscriptionStatus
import com.example.dormitoryapp.utils.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostSubscriptionViewModel(val app: Application) : AndroidViewModel(app) {

    val postSubscriptionOfUser = MutableLiveData<List<PostSubscriptionModel>>()
    val addPostSubscriptionResponse = MutableLiveData<ResponseModel>()
    val status = MutableLiveData<PostSubscriptionStatus>()

    fun getProfileSubscriptions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.getPostSubscriptionsByProfile(PrefsManager(app).getProfile().id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        postSubscriptionOfUser.value = response.body()
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

    fun addPostSubscription(idPost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sub = CreatePostSubscriptionModel(PrefsManager(app).getProfile().id, idPost)
                val response = DormitoryClient.retrofit.addPostSubscription(sub)
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

    fun deletePostSubscription(idPost: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sub = CreatePostSubscriptionModel(PrefsManager(app).getProfile().id, idPost)
                val response = DormitoryClient.retrofit.deletePostSubscription(sub)
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