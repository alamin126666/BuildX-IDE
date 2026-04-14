package com.buildx.ide.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buildx.ide.R
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount() = projects.size

    inner class ProjectViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val textViewProjectName: TextView = itemView.findViewById(R.id.textViewProjectName)
        private val textViewProjectPath: TextView = itemView.findViewById(R.id.textViewProjectPath)

        fun bind(project: ProjectFile) {
            textViewProjectName.text = project.name
            textViewProjectPath.text = project.path
            itemView.setOnClickListener { onItemClick(project) }
        }
    }
}
