package com.buildx.ide.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buildx.ide.databinding.ItemRecentProjectBinding
import com.buildx.ide.model.ProjectFile

class RecentProjectsAdapter(
    private val onItemClick: (ProjectFile) -> Unit
) : RecyclerView.Adapter<RecentProjectsAdapter.ProjectViewHolder>() {

    private var projects: List<ProjectFile> = emptyList()

    fun submitList(list: List<ProjectFile>) {
        projects = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemRecentProjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount() = projects.size

    inner class ProjectViewHolder(
        private val binding: ItemRecentProjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: ProjectFile) {
            binding.textViewProjectName.text = project.name
            binding.textViewProjectPath.text = project.path
            binding.root.setOnClickListener { onItemClick(project) }
        }
    }
}
