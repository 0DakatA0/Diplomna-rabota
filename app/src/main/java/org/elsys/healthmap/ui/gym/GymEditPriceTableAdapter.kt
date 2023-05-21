package org.elsys.healthmap.ui.gym

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.databinding.ItemPriceTableBinding
import java.math.BigDecimal
import java.text.DecimalFormat

class GymEditPriceTableAdapter(
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

            val decimalFormat = DecimalFormat("0.00")
            holder.binding.price = decimalFormat.format(item.second) + " лв."
        }

        holder.binding.root.setOnLongClickListener {
            if (item != null) {
                delete(item.first)
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = dataset.value?.size ?: 0
}