package com.androidide.ui.filemanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidide.R
import com.androidide.databinding.ItemFileBinding
import java.io.File

class FileAdapter(
    private val onFileClick: (File) -> Unit
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private val files = mutableListOf<FileItem>()
    private val expandedDirs = mutableSetOf<String>()

    data class FileItem(
        val file: File,
        val depth: Int
    )

    inner class ViewHolder(private val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FileItem) {
            binding.apply {
                // Indentação baseada na profundidade
                val params = root.layoutParams as ViewGroup.MarginLayoutParams
                params.marginStart = item.depth * 24
                root.layoutParams = params

                textFileName.text = item.file.name

                // Ícone baseado no tipo
                val iconRes = when {
                    item.file.isDirectory -> {
                        if (expandedDirs.contains(item.file.absolutePath)) {
                            R.drawable.ic_folder_open
                        } else {
                            R.drawable.ic_folder
                        }
                    }
                    item.file.extension == "kt" -> R.drawable.ic_kotlin
                    item.file.extension == "java" -> R.drawable.ic_java
                    item.file.extension == "xml" -> R.drawable.ic_xml
                    else -> R.drawable.ic_file
                }
                imageFileIcon.setImageResource(iconRes)

                root.setOnClickListener {
                    if (item.file.isDirectory) {
                        toggleDirectory(item.file)
                    } else {
                        onFileClick(item.file)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount() = files.size

    fun setFiles(fileList: List<File>) {
        files.clear()
        addFilesRecursively(fileList.firstOrNull()?.parentFile, 0)
        notifyDataSetChanged()
    }

    private fun addFilesRecursively(dir: File?, depth: Int) {
        dir?.listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
            ?.forEach { file ->
                files.add(FileItem(file, depth))
                if (file.isDirectory && expandedDirs.contains(file.absolutePath)) {
                    addFilesRecursively(file, depth + 1)
                }
            }
    }

    private fun toggleDirectory(dir: File) {
        if (expandedDirs.contains(dir.absolutePath)) {
            expandedDirs.remove(dir.absolutePath)
        } else {
            expandedDirs.add(dir.absolutePath)
        }
        
        // Recarregar lista
        val rootDir = files.firstOrNull()?.file?.parentFile
        files.clear()
        addFilesRecursively(rootDir, 0)
        notifyDataSetChanged()
    }

    fun setRootDirectory(dir: File) {
        files.clear()
        addFilesRecursively(dir, 0)
        notifyDataSetChanged()
    }
}
