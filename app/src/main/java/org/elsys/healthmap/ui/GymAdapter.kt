package org.elsys.healthmap.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.R

class GymAdapter (
    private val context: Context,
    private val dataset: List<Gym>
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {
    class GymViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gymNameAddress: TextView = view.findViewById(R.id.gymNameAddress)
        val ratingBar: RatingBar = view.findViewById(R.id.gymRatingBar)
        val gymCard: CardView = view.findViewById(R.id.gymCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gym, parent,false)

        return GymViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        holder.gymCard.setOnClickListener {
            val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment()
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount() = dataset.size
}