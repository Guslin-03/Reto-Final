package com.example.reto_final.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto_final.data.model.user.User
import com.example.reto_final.databinding.ItemUserFragmentBinding

class UserFragmentAdapter(
    private val onClickListener: (User) -> Unit,
) : ListAdapter<User, UserFragmentAdapter.UserFragmentViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFragmentViewHolder {
        val binding =
            ItemUserFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFragmentAdapter.UserFragmentViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

        holder.itemView.setOnClickListener {
            onClickListener(user)
        }

    }

    inner class UserFragmentViewHolder(private val binding: ItemUserFragmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val userNameAndSurname = user.name + " " +user.surname
            binding.userName.text = userNameAndSurname
            binding.userPhoneNumber.text = user.phoneNumber.toString()
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return (oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.surname == newItem.surname &&
                    oldItem.email == newItem.email &&
                    oldItem.phoneNumber == newItem.phoneNumber)
        }

    }

}