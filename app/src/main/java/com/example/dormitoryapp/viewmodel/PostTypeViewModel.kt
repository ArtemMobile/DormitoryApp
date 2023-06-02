package com.example.dormitoryapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.PostTypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostTypeViewModel : ViewModel() {
    val types = MutableLiveData<List<PostTypeModel>>()

    fun getPostTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DormitoryClient.retrofit.getPostTypes()
                withContext(Dispatchers.Main){
                    if (response.isSuccessful) {
                        types.value = response.body()
                    } else {

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}