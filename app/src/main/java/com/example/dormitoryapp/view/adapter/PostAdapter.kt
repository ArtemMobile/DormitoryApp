package com.example.dormitoryapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.dormitoryapp.databinding.PostCardBinding
import com.example.dormitoryapp.model.dto.PostModel
import java.time.LocalDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

class PostAdapter(val context: Context, var posts: List<PostModel>, val onClick: (PostModel) -> Unit = {}) :
    RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(val binding: PostCardBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(PostCardBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = posts.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            tvTitle.text = post.title
            tvDescription.text = post.description

            tvDate.text = try {
               "Дата публикации: " + DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    .format(LocalDateTime.parse(post.publishDate))
            } catch (e:java.lang.Exception){
               "Дата публикации: " + DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    .format(OffsetTime.parse(post.publishDate))
            }

            root.setOnClickListener {
                onClick(post)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosts(list: List<PostModel>) {
        this.posts = list
        notifyDataSetChanged()
    }
}