package org.elsys.healthmap.ui.gym

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.ItemGymBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.ImagesRepository
import org.elsys.healthmap.ui.viewmodels.GymsViewModel
import java.io.File

class GymAdapter (
    // FIXME do not use LiveData here, you will not be able to handle updates properly
    //  Observe the LiveData in the fragment/activity then pass the actual data that you want
    //  to display to the adapter by invoking an update function
    private val dataset: LiveData<Map<String, Gym>>,
    private val cacheDir: File,
    // FIXME don't pass the ViewModel to the adapter
    private val viewModel: GymsViewModel,
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

        // FIXME usually we would add a method bind(element) to the ViewHolder and just invoke
        //  it here with the appropriate element
        val id = element?.first
        val gym = element?.second
        holder.binding.gym = gym

        // FIXME you can use gym?.photos?.firstOrNull()?.let { File(...) }
        val file = if(gym?.photos?.isNotEmpty() == true){
            File(cacheDir, gym.photos[0])
        } else {
            null
        }

        // FIXME The image download logic does not belong here, this should be done in the ViewModel
        //  and gym data should be updated when needed
        if (file != null && gym?.photos?.isNotEmpty() == true) {
            gym.photos[0].let {
                if (!file.exists()) {
                    viewModel.viewModelScope.launch {
                        ImagesRepository.getImage(it, file)
                        holder.binding.imageView.setImageURI(file.toUri())
                    }
                } else {
                    holder.binding.imageView.setImageURI(file.toUri())
                }
            }
        }

        // FIXME pass click handling function to the adapter constructor and invoke it here like delete
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
            // FIXME omit the return keyword
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}