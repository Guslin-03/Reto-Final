package com.example.reto_final.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.reto_final.data.model.user.User
import com.example.reto_final.databinding.ItemUserBinding
import androidx.recyclerview.widget.ListAdapter

class UserAdapter(
    private val onIsAdmin: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

    }

    inner class UserViewHolder(private val binding:ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val userNameAndSurname = user.name + " " +user.surname
            binding.userName.text = userNameAndSurname
            binding.userPhoneNumber.text = user.phone_number1.toString()

            binding.imageViewRemoveUser.setOnClickListener {
                onIsAdmin(user)
            }

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
                    oldItem.phone_number1 == newItem.phone_number1)
        }

    }

}
