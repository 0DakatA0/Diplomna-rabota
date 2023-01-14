package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.databinding.ItemGymBinding
import org.elsys.healthmap.ui.Gym

class GymAdapter (
    private val dataset: LiveData<List<Gym>>
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {
    class GymViewHolder(val binding: ItemGymBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val binding = ItemGymBinding.inflate(LayoutInflater.from(parent.context),
            parent,false)

        return GymViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        val gym = dataset.value?.get(position)
        holder.binding.gym = gym

//        holder.gymCard.setOnClickListener {
//            val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment()
//            holder.itemView.findNavController().navigate(action)
//        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}