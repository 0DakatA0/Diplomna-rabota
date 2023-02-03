package org.elsys.healthmap.ui.gym

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.ItemGymBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File

class GymAdapter (
    private val dataset: LiveData<Map<String, Gym>>,
    private val context: Context,
    private val viewModel: GymsViewModel,
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
            File(context.cacheDir, gym.photos[0])
        } else {
            null
        }

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
            val builder = AlertDialog.Builder(context)

            builder.apply {
                setPositiveButton("Yes") { _, _ ->
                    if (id != null) {
                        viewModel.viewModelScope.launch {
                            GymsRepository.deleteGym(id)

                            for (photo in gym?.photos!!) {
                                ImagesRepository.deleteImage(photo)
                            }

                            viewModel.deleteGym(id)
                        }
                    }
                }
                setNegativeButton("No") { _, _ ->

                }
            }
            builder.setMessage("Are you shore that you want to delete that gym?")
            builder.create().show()

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}