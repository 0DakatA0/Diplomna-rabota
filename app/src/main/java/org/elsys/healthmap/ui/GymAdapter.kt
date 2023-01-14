package org.elsys.healthmap.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.ItemGymBinding

class GymAdapter (
    private val context: Context,
    private val dataset: List<Gym>
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {
    class GymViewHolder(val binding: ItemGymBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val binding = ItemGymBinding.inflate(LayoutInflater.from(parent.context),
            parent,false)

        return GymViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        val gym = dataset[position]
        holder.binding.gym = gym
//        holder.itemView.apply {
//            val gym = dataset[position]
//
//            Log.d(".GymAdapter", gym.name)
//
//            holder.gymNameAddress.text = gym.name
//            holder.ratingBar.rating = gym.rating
//
//            holder.gymCard.setOnClickListener {
//                val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment()
//                holder.itemView.findNavController().navigate(action)
//            }
//        }
    }

    override fun getItemCount() = dataset.size
}