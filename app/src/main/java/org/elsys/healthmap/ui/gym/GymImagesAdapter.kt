package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.ItemGymPictureBinding
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File

class GymImagesAdapter (
    private val dataset: LiveData<List<String>>,
    private val cacheDir: File,
    private val scope: CoroutineScope,
    private val onFail: (Int) -> Unit,
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<GymImagesAdapter.GymPictureViewHolder>() {
    class GymPictureViewHolder(val binding: ItemGymPictureBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymPictureViewHolder {
        val binding = ItemGymPictureBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return GymPictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymPictureViewHolder, position: Int) {
        val file = dataset.value?.get(position)?.let { File(cacheDir, it) }

        if (file != null) {
            if(!file.exists()) {
                scope.launch {
                    try {
                        dataset.value?.get(position)?.let { ImagesRepository.getImage(it, file) }
                        if(holder.adapterPosition != position) return@launch
                        holder.binding.root.setImageURI(file.toUri())
                    } catch (e: StorageException) {
                        onFail(holder.adapterPosition)
                    }
                }
            } else {
                holder.binding.root.setImageURI(file.toUri())
            }
        }

        holder.binding.root.setOnLongClickListener {
            dataset.value?.get(position)?.let { listener(it) }
            return@setOnLongClickListener true;
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}