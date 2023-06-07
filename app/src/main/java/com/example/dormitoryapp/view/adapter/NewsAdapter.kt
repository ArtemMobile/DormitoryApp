package com.example.dormitoryapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitoryapp.databinding.NewsCardBinding
import com.example.dormitoryapp.model.dto.NewsModel

class NewsAdapter(val context: Context, var list: List<NewsModel>, val onClick: (NewsModel) -> Unit = {}) :
    RecyclerView.Adapter<NewsAdapter.NewsHolder>() {
    class NewsHolder(val binding: NewsCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        return NewsHolder(NewsCardBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvTitle.text = item.title
            tvContent.text = item.content
            btnVerbose.setOnClickListener {
                onClick(item)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(news: List<NewsModel>){
        this.list = news
        notifyDataSetChanged()
    }
}