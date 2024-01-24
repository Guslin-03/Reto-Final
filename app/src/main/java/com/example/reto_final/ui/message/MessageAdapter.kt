package com.example.reto_final.ui.message

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.databinding.ItemMessageBinding
import com.example.reto_final.utils.MyApp

class MessageAdapter(
    private val selectedGroup: Group
)
    : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding =
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)

    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {

            binding.text.text = message.text
            if (MyApp.userPreferences.getUser()?.id == message.authorId) {
                binding.text.setTextColor(Color.parseColor("#FF0000"))
                binding.text.gravity = Gravity.END
            }else {
                binding.text.setTextColor(Color.parseColor("#0000FF"))
                binding.text.gravity = Gravity.START
            }

        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return (oldItem.id == newItem.id && oldItem.text == newItem.text && oldItem.groupId == newItem.groupId)
        }

    }

}