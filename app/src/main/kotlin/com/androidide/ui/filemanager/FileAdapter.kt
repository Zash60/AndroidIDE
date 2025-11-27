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
    private val onFileReloadNeeded: () -> Unit // Callback para recarregar a lista
) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    // Lista linear que representa a árvore (Flattened Tree)
    private val visibleItems = mutableListOf<FileNode>()
    private var rootDir: File? = null

    // Classe auxiliar para controle visual
    data class FileNode(
        val file: File,
        val depth: Int, // Nível de indentação
        var isExpanded: Boolean = false,
        var hasChildren: Boolean = false
    )

    inner class ViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(node: FileNode) {
            val file = node.file
            
            // Indentação (Margem esquerda baseada na profundidade)
            val indent = node.depth * 40 // 40px por nível
            binding.root.setPadding(indent, 0, 0, 0)
            
            binding.textFileName.text = file.name
            
            // Ícones
            if (file.isDirectory) {
                binding.imageFileIcon.setImageResource(if (node.isExpanded) R.drawable.ic_folder_open else R.drawable.ic_folder)
                binding.arrowIcon.visibility = View.VISIBLE
                binding.arrowIcon.rotation = if (node.isExpanded) 90f else 0f
                
                // Esconde a seta se a pasta estiver vazia (opcional, aqui assume que pastas podem ter conteúdo)
                binding.arrowIcon.alpha = 1.0f
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

            // Clique
            binding.root.setOnClickListener {
                if (file.isDirectory) {
                    toggleFolder(adapterPosition, node)
                } else {
                    onFileClick(file)
                }
            }

            // Menu de Contexto (Renomear/Excluir)
            binding.root.setOnLongClickListener { view ->
                showContextMenu(view, file)
                true
            }
        }

        private fun showContextMenu(view: View, file: File) {
            val popup = PopupMenu(context, view)
            popup.menu.add("Renomear")
            popup.menu.add("Excluir")
            
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Renomear" -> showRenameDialog(file)
                    "Excluir" -> showDeleteDialog(file)
                }
                true
            }
            popup.show()
        }

        private fun showRenameDialog(file: File) {
            val input = android.widget.EditText(context)
            input.setText(file.name)
            
            AlertDialog.Builder(context)
                .setTitle("Renomear")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val newName = input.text.toString()
                    if (newName.isNotEmpty() && newName != file.name) {
                        val newFile = File(file.parentFile, newName)
                        if (file.renameTo(newFile)) {
                            onFileReloadNeeded()
                        } else {
                            Toast.makeText(context, "Erro ao renomear", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        private fun showDeleteDialog(file: File) {
            AlertDialog.Builder(context)
                .setTitle("Excluir")
                .setMessage("Tem certeza que deseja excluir '${file.name}'?")
                .setPositiveButton("Excluir") { _, _ ->
                    if (file.deleteRecursively()) {
                        onFileReloadNeeded()
                    } else {
                        Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun toggleFolder(position: Int, node: FileNode) {
        if (node.isExpanded) {
            // Colapsar: Remover todos os filhos visíveis abaixo deste nó
            var count = 0
            val start = position + 1
            
            // Remove itens enquanto a profundidade for maior que a do pai
            while (start < visibleItems.size && visibleItems[start].depth > node.depth) {
                visibleItems.removeAt(start)
                count++
            }
            
            node.isExpanded = false
            notifyItemRangeRemoved(start, count)
            notifyItemChanged(position) // Atualiza ícone da pasta pai
            
        } else {
            // Expandir: Ler disco e adicionar filhos
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
        
        // Adiciona apenas o primeiro nível inicialmente
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
