package com.androidide.ui.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidide.databinding.ItemProjectBinding
import com.androidide.project.Project
import java.text.SimpleDateFormat
import java.util.*

class ProjectAdapter(
    private val projects: List<Project>,
    private val onProjectClick: (Project) -> Unit,
    private val onProjectLongClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class ViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.apply {
                textProjectName.text = project.name
                textPackageName.text = project.packageName
                textLastModified.text = "Modificado: ${dateFormat.format(Date(project.lastModified))}"
                textSdkVersion.text = "SDK: ${project.minSdk} - ${project.targetSdk}"

                root.setOnClickListener { onProjectClick(project) }
                root.setOnLongClickListener {
                    onProjectLongClick(project)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount() = projects.size
}
