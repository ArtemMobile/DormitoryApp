package com.example.dormitoryapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dormitoryapp.model.api.DormitoryClient
import com.example.dormitoryapp.model.dto.PostModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class PostViewModelObservable(val app: Application): AndroidViewModel(app) {
    private var mPosts = MutableLiveData<MutableList<PostModel>>()
    val posts = mPosts
    private var mError = MutableLiveData<String>()
    val error = mError
    private var mPost = MutableLiveData<PostModel>()
    val post = mPost

    private fun getPostsObservable(): Observable<PostModel> {
        return DormitoryClient.retrofit
            .getPosts()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMap { posts ->
                mPosts.value = posts
                Observable.fromIterable(posts)
                    .subscribeOn(Schedulers.io())
            }
    }

    private fun getPostType(post: PostModel): Observable<PostModel> {
        return DormitoryClient.retrofit
            .getPostType(post.id)
            .map {
                post.name = it.name
                post
            }
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun getPosts() {
        getPostsObservable()
            .flatMap { post -> getPostType(post) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { post -> mPost.value = post },
                onError = { e -> mError.value = e.localizedMessage }
            )
    }
}