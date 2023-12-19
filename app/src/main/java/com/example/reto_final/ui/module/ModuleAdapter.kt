package com.example.reto_final.ui.module

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto_final.data.Module
import com.example.reto_final.databinding.ItemModuleBinding

class ModuleAdapter : ListAdapter<Module, ModuleAdapter.ModuleViewHolder>(SongDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding =
            ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleAdapter.ModuleViewHolder, position: Int) {
        val module = getItem(position)
        holder.bind(module)
    }

    inner class ModuleViewHolder(private val binding: ItemModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Module) {
            binding.textViewName.text = module.name
        }
    }

}

class SongDiffCallback : DiffUtil.ItemCallback<Module>() {

    override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
        return (oldItem.id == newItem.id && oldItem.name == newItem.name)
    }

}