package com.example.dormitoryapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitoryapp.databinding.UserCardBinding
import com.example.dormitoryapp.model.dto.ProfileModel

class UserAdapter(
    val context: Context,
    var users: List<ProfileModel>,
    val onClick: (ProfileModel) -> Unit = {}
) :
    RecyclerView.Adapter<UserAdapter.UserHolder>() {
    class UserHolder(val binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(UserCardBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            tvName.text = "${user.firstName} ${user.surname}"
            tvRoom.text = "${user.room} комната"

            root.setOnClickListener {
                onClick(user)
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ProfileModel>) {
        this.users = list
        notifyDataSetChanged()
    }
}