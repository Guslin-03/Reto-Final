package com.example.reto_final.ui.group

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto_final.data.model.Group
import com.example.reto_final.databinding.ItemGroupBinding

class GroupAdapter(
    private val onClickListener: (Group) -> Unit
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    private var lastSelectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding =
            ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupAdapter.GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)

        if (position == lastSelectedPosition) {
            // Establece el color de fondo para el elemento seleccionado
            holder.itemView.setBackgroundColor(Color.RED)
        } else {
            // Establece el color de fondo para otros elementos (por defecto)
            holder.itemView.setBackgroundColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener {
            onClickListener(group)

            val previousSelectedPosition = lastSelectedPosition
            lastSelectedPosition = holder.adapterPosition

            //Notificamos de la actualizacion del ultimo y primer elemento
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(lastSelectedPosition)

        }

    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: Group) {
            binding.groupName.text = group.name
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {

        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return (oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.groupType == newItem.groupType)
        }

    }

}