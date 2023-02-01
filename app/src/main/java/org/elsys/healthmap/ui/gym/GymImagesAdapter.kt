package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.ItemGymPictureBinding
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File

class GymImagesAdapter (
    private val dataset: MutableList<String>,
    private val cacheDir: File,
    private val scope: CoroutineScope,
    private var isChanged: Boolean
) : RecyclerView.Adapter<GymImagesAdapter.GymPictureViewHolder>() {
    class GymPictureViewHolder(val binding: ItemGymPictureBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymPictureViewHolder {
        val binding = ItemGymPictureBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return GymPictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymPictureViewHolder, position: Int) {
        val file = File(cacheDir, dataset[position])

        if(!file.exists()) {
            scope.launch {
                ImagesRepository.getImage(dataset[position], file)
                holder.binding.root.setImageURI(file.toUri())
            }
        } else {
            holder.binding.root.setImageURI(file.toUri())
        }

        holder.binding.root.setOnLongClickListener {
            scope.launch {
                ImagesRepository.deleteImage(dataset[position])
                dataset.removeAt(position)
                notifyItemRemoved(position)
                file.delete()
                isChanged = true
            }

            return@setOnLongClickListener true;
        }
    }

    override fun getItemCount() = dataset.size
}