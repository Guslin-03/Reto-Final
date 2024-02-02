package com.example.reto_final.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.databinding.ItemMessageBinding
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.MyApp.Companion.context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val onClickListener: (Message) -> Unit
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
        holder.itemView.setOnClickListener {
            onClickListener(message)
        }
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {

            binding.text.text = message.text

            if (message.text.startsWith(context.getExternalFilesDir(null).toString()+ "/RetoFinalImage")){
                Glide.with(context)
                    .load(File(message.text))
                    .into(binding.image)
                binding.text.visibility = View.GONE
                binding.image.visibility = View.VISIBLE
            }else if (message.text.startsWith(context.getExternalFilesDir(null).toString()+ "/RetoFinalPdf")){
                binding.text.text="Descargar PDF"
            }

            if (MyApp.userPreferences.getUser()?.id == message.userId) {
                binding.name.visibility = View.GONE
                if(message.saved!==null){
                    binding.sentHour.text = parseDate(message.sent)
                }else{
                    binding.sentHour.text = parseDate(message.sent)
                    binding.clock.visibility=View.VISIBLE
                }

                val drawable = ContextCompat.getDrawable(context, R.drawable.background_sent)
                binding.linearLayout1.background = drawable

            }else {

                val linearLayout = binding.linearLayout1
                val layoutParams = linearLayout.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
                linearLayout.layoutParams = layoutParams

                val drawable = ContextCompat.getDrawable(context, R.drawable.background_received)
                binding.linearLayout1.background = drawable

                binding.name.text = message.userId.toString()

                binding.sentHour.text = message.saved?.let { parseDate(it) }

            }

        }

        private fun parseDate(hour: Long): String {

            val hora = Date(hour)
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            return formatter.format(hora)

        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return (oldItem.id == newItem.id && oldItem.text == newItem.text && oldItem.chatId == newItem.userId)
        }

    }

}