package com.example.reto_final.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.databinding.ItemGroupBinding

class GroupAdapter(
    private val onClickListener: (Group) -> Unit
) : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding =
            ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupAdapter.GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)

        holder.itemView.setOnClickListener {
            onClickListener(group)
        }

    }

    fun filtrateTypeGroup(listGroups: List<Group>?, typeGroup: ChatEnumType):List<Group> {

        val filteredGroups: List<Group>?

        if(listGroups != null) {
            filteredGroups = listGroups.filter { it.type == typeGroup.toString() }
            submitList(filteredGroups.toList())

        }else {
            filteredGroups = currentList.filter { it.type == typeGroup.toString() }
            submitList(filteredGroups.toList())
        }
        return filteredGroups
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: Group) {
            binding.groupName.text = group.name

            if (group.type == ChatEnumType.PRIVATE.toString()) {
                binding.groupType.setImageResource(R.drawable.privategroup)
            } else {
                binding.groupType.visibility = View.INVISIBLE
            }
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {

        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return (oldItem.id == newItem.id
                    && oldItem.name == newItem.name
                    && oldItem.type == newItem.type
                    && oldItem.adminId == newItem.adminId)
        }

    }

}