package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.databinding.ItemPriceTableBinding

class GymEditPriceTableAdapter(
    // FIXME do not use LiveData here, you will not be able to handle updates properly
    //  Observe the LiveData in the fragment/activity then pass the actual data that you want
    //  to display to the adapter by invoking an update function
    private val dataset: LiveData<Map<String, Float>>,
    private val delete: (String) -> Unit,
) : RecyclerView.Adapter<GymEditPriceTableAdapter.PriceTableViewHolder>() {
    class PriceTableViewHolder(val binding: ItemPriceTableBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceTableViewHolder {
        val binding = ItemPriceTableBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return PriceTableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PriceTableViewHolder, position: Int) {
        val item = dataset.value?.toList()?.get(position)
        if (item != null) {
            holder.binding.product = item.first
            holder.binding.price = item.second.toString()
        }

        holder.binding.root.setOnLongClickListener {
            if (item != null) {
                delete(item.first)
            }
            // FIXME omit the return keyword
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}