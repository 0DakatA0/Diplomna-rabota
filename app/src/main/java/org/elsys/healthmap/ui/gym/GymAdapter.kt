package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.databinding.ItemGymBinding
import org.elsys.healthmap.models.Gym

class GymAdapter (
    private val dataset: LiveData<Map<String, Gym>>
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {
    class GymViewHolder(val binding: ItemGymBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val binding = ItemGymBinding.inflate(LayoutInflater.from(parent.context),
            parent,false)

        return GymViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        val element = dataset.value?.toList()?.get(position)

        val key = element?.first
        val gym = element?.second
        holder.binding.gym = gym

        holder.binding.root.setOnClickListener {
            val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment(key)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}