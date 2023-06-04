package com.example.dormitoryapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.CreatePostModel
import com.example.dormitoryapp.model.dto.PostModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.utils.CreatePostStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel : ViewModel() {
    val posts = MutableLiveData<List<PostModel>>()
    val error = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    val createPostResponse = MutableLiveData<ResponseModel>()
    val createPostStatus = MutableLiveData<CreatePostStatus>()
    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue(true)
                val response = DormitoryClient.retrofit.getPostsData()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        posts.value = response.body()!!
                        isLoading.postValue(false)
                    } else {
                        Log.d("getPostsError", response.errorBody()?.string().toString())
                    }
                }
            } catch (e: Exception) {
                //error.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun getPostByType(idType: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue(true)
                val response = DormitoryClient.retrofit.getPostsByType(idType)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        posts.value = response.body()!!
                        isLoading.postValue(false)
                    } else {
                        Log.d("getPostsByTypeError", response.errorBody()?.string().toString())
                    }
                }
            } catch (e: Exception) {
                //error.postValue(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }


    fun createPost(createPostModel: CreatePostModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.postValue(true)
                val response = DormitoryClient.retrofit.createPost(createPostModel)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        createPostResponse.value = response.body()
                        createPostStatus.value = CreatePostStatus.SUCCESS
                        Log.d("cretatedPost", createPostModel.toString())
                        isLoading.value = false
                    } else {
                        createPostStatus.value = CreatePostStatus.FAILURE
                        Log.d("createPostFailure", response.errorBody()?.string().toString())
                    }
                }

            } catch (e: java.lang.Exception) {
                e.localizedMessage?.let { Log.d("createPostException", it) }
            }
        }
    }

    fun clearCreatePostStatus(){
        createPostStatus.value = CreatePostStatus.NOTHING
    }
}