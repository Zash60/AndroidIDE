package com.androidide.ui.filemanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.androidide.R
import com.androidide.databinding.ItemFileBinding
import java.io.File

class FileAdapter(
    private val context: Context,
    private val onFileClick: (File) -> Unit,
    private val onFileAction: (Action, File) -> Unit // Callback genérico para ações
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    enum class Action { RENAME, DELETE, NEW_FILE }

    private val visibleItems = mutableListOf<FileNode>()
    private var rootDir: File? = null

    data class FileNode(
        val file: File,
        val depth: Int,
        var isExpanded: Boolean = false
    )

    inner class ViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(node: FileNode) {
            val file = node.file
            
            // Indentação visual
            val indent = node.depth * 40
            binding.root.setPadding(indent, 0, 0, 0)
            
            binding.textFileName.text = file.name
            
            if (file.isDirectory) {
                binding.imageFileIcon.setImageResource(if (node.isExpanded) R.drawable.ic_folder_open else R.drawable.ic_folder)
                binding.arrowIcon.visibility = View.VISIBLE
                binding.arrowIcon.rotation = if (node.isExpanded) 90f else 0f
            } else {
                binding.arrowIcon.visibility = View.INVISIBLE
                val iconRes = when (file.extension) {
                    "kt" -> R.drawable.ic_kotlin
                    "java" -> R.drawable.ic_java
                    "xml" -> R.drawable.ic_xml
                    else -> R.drawable.ic_file
                }
                binding.imageFileIcon.setImageResource(iconRes)
            }

            // Clique simples (Expandir pasta ou Abrir arquivo)
            binding.root.setOnClickListener {
                if (file.isDirectory) {
                    toggleFolder(adapterPosition, node)
                } else {
                    onFileClick(file)
                }
            }

            // Clique Longo (Menu de Contexto)
            binding.root.setOnLongClickListener { view ->
                showContextMenu(view, file)
                true
            }
        }

        private fun showContextMenu(view: View, file: File) {
            val popup = PopupMenu(context, view)
            
            // Se for pasta, permite criar arquivo dentro dela
            if (file.isDirectory) {
                popup.menu.add("Novo Arquivo/Pasta")
            }
            popup.menu.add("Renomear")
            popup.menu.add("Excluir")
            
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Novo Arquivo/Pasta" -> onFileAction(Action.NEW_FILE, file)
                    "Renomear" -> onFileAction(Action.RENAME, file)
                    "Excluir" -> onFileAction(Action.DELETE, file)
                }
                true
            }
            popup.show()
        }
    }

    private fun toggleFolder(position: Int, node: FileNode) {
        if (node.isExpanded) {
            // Colapsar
            var count = 0
            val start = position + 1
            while (start < visibleItems.size && visibleItems[start].depth > node.depth) {
                visibleItems.removeAt(start)
                count++
            }
            node.isExpanded = false
            notifyItemRangeRemoved(start, count)
            notifyItemChanged(position)
        } else {
            // Expandir
            val children = node.file.listFiles()
                ?.sortedWith(compareBy({ !it.isDirectory }, { it.name })) 
                ?: return

            if (children.isEmpty()) return

            node.isExpanded = true
            var insertIndex = position + 1
            for (child in children) {
                visibleItems.add(insertIndex, FileNode(child, node.depth + 1))
                insertIndex++
            }
            notifyItemRangeInserted(position + 1, children.size)
            notifyItemChanged(position)
        }
    }

    fun loadDirectory(root: File) {
        this.rootDir = root
        visibleItems.clear()
        root.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name }))?.forEach { file ->
            visibleItems.add(FileNode(file, 0))
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(visibleItems[position])
    }

    override fun getItemCount() = visibleItems.size
}
