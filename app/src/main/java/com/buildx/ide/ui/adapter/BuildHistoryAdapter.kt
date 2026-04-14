package com.buildx.ide.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buildx.ide.R
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_build_history, parent, false)
        return BuildViewHolder(view)
    }

    override fun onBindViewHolder(holder: BuildViewHolder, position: Int) {
        holder.bind(builds[position])
    }

    override fun getItemCount() = builds.size

    inner class BuildViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val textViewBuildId: TextView = itemView.findViewById(R.id.textViewBuildId)
        private val textViewBuildStatus: TextView = itemView.findViewById(R.id.textViewBuildStatus)

        fun bind(build: BuildOutput) {
            textViewBuildId.text = "Build: ${build.id.take(8)}"
            textViewBuildStatus.text = build.status.name
            val statusColor = when (build.status) {
                BuildStatus.SUCCESS -> 0xFF4CAF50.toInt()
                BuildStatus.FAILED -> 0xFFF44336.toInt()
                BuildStatus.RUNNING -> 0xFFFF9800.toInt()
                else -> 0xFF2196F3.toInt()
            }
            textViewBuildStatus.setTextColor(statusColor)
            itemView.setOnClickListener { onItemClick(build) }
        }
    }
}
