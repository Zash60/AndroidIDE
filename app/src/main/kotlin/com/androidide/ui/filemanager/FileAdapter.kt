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

    private val items = mutableListOf<FileItem>()

    data class FileItem(val file: File, val depth: Int)

    inner class ViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileItem) {
            val padding = item.depth * 24
            binding.root.setPadding(padding, 8, 16, 8)
            binding.textFileName.text = item.file.name
            
            val iconRes = when {
                item.file.isDirectory -> R.drawable.ic_folder
                item.file.extension == "kt" -> R.drawable.ic_kotlin
                item.file.extension == "java" -> R.drawable.ic_java
                item.file.extension == "xml" -> R.drawable.ic_xml
                else -> R.drawable.ic_file
            }
            binding.imageFileIcon.setImageResource(iconRes)
            binding.root.setOnClickListener { onFileClick(item.file) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    fun setFiles(files: List<File>, rootDir: File) {
        items.clear()
        files.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { file ->
            val depth = file.absolutePath.removePrefix(rootDir.absolutePath).count { it == '/' }
            items.add(FileItem(file, depth))
        }
        notifyDataSetChanged()
    }
}
