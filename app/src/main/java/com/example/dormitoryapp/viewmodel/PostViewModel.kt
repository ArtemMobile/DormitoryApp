package com.example.dormitoryapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel : ViewModel() {

    val posts = MutableLiveData<List<PostModel>>()
    val error = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                delay(1500)
                val response = DormitoryClient.retrofit.getPostsData()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        posts.value = response.body()!!
                        isLoading.postValue(false)
                    } else {
                        error.value = "error"
                    }
                }
            } catch (e: Exception) {
                error.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun getPostByType(idType: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue(true)
                delay(1500)
                val response = DormitoryClient.retrofit.getPostsByType(idType)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        posts.value = response.body()!!
                        isLoading.postValue(false)
                    } else {
                        error.value = "error"
                    }
                }
            } catch (e: Exception) {
                error.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }


}