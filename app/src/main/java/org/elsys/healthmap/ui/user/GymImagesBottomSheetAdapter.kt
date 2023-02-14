package org.elsys.healthmap.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.ItemGymPictureBinding
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File

class GymImagesBottomSheetAdapter(
    private val scope: CoroutineScope,
    private val cacheDir: File,
    private val dataset: List<String>,
) : RecyclerView.Adapter<GymImagesBottomSheetAdapter.GymImagesBottomSheetViewHolder>() {

    class GymImagesBottomSheetViewHolder(val binding: ItemGymPictureBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GymImagesBottomSheetViewHolder {
        val binding = ItemGymPictureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return GymImagesBottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymImagesBottomSheetViewHolder, position: Int) {
        val file =  File(cacheDir, dataset[position])

        if(!file.exists()) {
            scope.launch {
                ImagesRepository.getImage(dataset[position], file)
                holder.binding.root.setImageURI(file.toUri())
            }
        } else {
            holder.binding.root.setImageURI(file.toUri())
        }
    }

    override fun getItemCount() = dataset.size
}