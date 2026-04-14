package com.buildx.ide.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buildx.ide.databinding.ItemBuildHistoryBinding
import com.buildx.ide.model.BuildOutput
import com.buildx.ide.model.BuildStatus

class BuildHistoryAdapter(
    private val onItemClick: (BuildOutput) -> Unit
) : RecyclerView.Adapter<BuildHistoryAdapter.BuildViewHolder>() {

    private var builds: List<BuildOutput> = emptyList()

    fun submitList(list: List<BuildOutput>) {
        builds = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildViewHolder {
        val binding = ItemBuildHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BuildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuildViewHolder, position: Int) {
        holder.bind(builds[position])
    }

    override fun getItemCount() = builds.size

    inner class BuildViewHolder(
        private val binding: ItemBuildHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(build: BuildOutput) {
            binding.textViewBuildId.text = "Build: ${build.id.take(8)}"
            binding.textViewBuildStatus.text = build.status.name
            val statusColor = when (build.status) {
                BuildStatus.SUCCESS -> 0xFF4CAF50.toInt()
                BuildStatus.FAILED -> 0xFFF44336.toInt()
                BuildStatus.RUNNING -> 0xFFFF9800.toInt()
                else -> 0xFF2196F3.toInt()
            }
            binding.textViewBuildStatus.setTextColor(statusColor)
            binding.root.setOnClickListener { onItemClick(build) }
        }
    }
}
