package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.ItemGymBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File

class GymAdapter (
    private val dataset: LiveData<Map<String, Gym>>,
    private val cacheDir: File,
    private val scope: CoroutineScope,
    private val delete: (String) -> Unit
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {
    class GymViewHolder(val binding: ItemGymBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val binding = ItemGymBinding.inflate(LayoutInflater.from(parent.context),
            parent,false)

        return GymViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        val element = dataset.value?.toList()?.get(position)

        val id = element?.first
        val gym = element?.second
        holder.binding.gym = gym

        val file = if(gym?.photos?.isNotEmpty() == true){
            File(cacheDir, gym.photos[0])
        } else {
            null
        }

        if (file != null && gym?.photos?.isNotEmpty() == true) {
            gym.photos[0].let {
                if (!file.exists()) {
                    scope.launch {
                        ImagesRepository.getImage(it, file)
                        holder.binding.imageView.setImageURI(file.toUri())
                    }
                } else {
                    holder.binding.imageView.setImageURI(file.toUri())
                }
            }
        }

        holder.binding.root.setOnClickListener {
            val action = id?.let { it1 ->
                GymsFragmentDirections.actionGymsFragmentToGymEditFragment(
                    it1
                )
            }
            if (action != null) {
                holder.itemView.findNavController().navigate(action)
            }
        }

        holder.binding.root.setOnLongClickListener {
            if (id != null) {
                delete(id)
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}