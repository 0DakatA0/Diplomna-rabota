package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.databinding.ItemPriceTableBinding

class GymEditPriceTableAdapter(
    private val dataset: Map<String, Float>
) : RecyclerView.Adapter<GymEditPriceTableAdapter.PriceTableViewHolder>() {
    class PriceTableViewHolder(val binding: ItemPriceTableBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceTableViewHolder {
        val binding = ItemPriceTableBinding.inflate(LayoutInflater.from(parent.context),
            parent,false)

        return PriceTableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PriceTableViewHolder, position: Int) {
        val item = dataset.toList()[position]
        holder.binding.product = item.first
        holder.binding.price = item.second.toString()
    }

    override fun getItemCount() = dataset.size
}