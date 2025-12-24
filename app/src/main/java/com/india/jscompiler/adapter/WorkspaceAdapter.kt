package com.india.jscompiler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.india.jscompiler.data.WorkspaceEntity
import com.india.jscompiler.databinding.ItemWorkspaceBinding
import java.text.SimpleDateFormat
import java.util.*

class WorkspaceAdapter(private val onWorkspaceClick: (WorkspaceEntity) -> Unit) :
    ListAdapter<WorkspaceEntity, WorkspaceAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemWorkspaceBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        fun bind(workspace: WorkspaceEntity, onClick: (WorkspaceEntity) -> Unit) {
            binding.tvTitle.text = workspace.title
            binding.tvSubtitle.text = "Last updated: ${dateFormat.format(Date(workspace.lastUpdated))}"
            binding.root.setOnClickListener { onClick(workspace) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkspaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onWorkspaceClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<WorkspaceEntity>() {
        override fun areItemsTheSame(oldItem: WorkspaceEntity, newItem: WorkspaceEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkspaceEntity, newItem: WorkspaceEntity): Boolean {
            return oldItem == newItem
        }
    }
}
