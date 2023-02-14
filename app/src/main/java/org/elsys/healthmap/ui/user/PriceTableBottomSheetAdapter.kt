package org.elsys.healthmap.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.elsys.healthmap.databinding.ItemPriceTableBinding


class PriceTableBottomSheetAdapter(
    private val dataset: Map<String, Float>,
) : RecyclerView.Adapter<PriceTableBottomSheetAdapter.PriceTableBottomSheetViewHolder>() {

    class PriceTableBottomSheetViewHolder(val binding: ItemPriceTableBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PriceTableBottomSheetViewHolder {
        val binding = ItemPriceTableBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return PriceTableBottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PriceTableBottomSheetViewHolder, position: Int) {
        val item = dataset.toList()[position]
        holder.binding.product = item.first
        holder.binding.price = item.second.toString()
    }

    override fun getItemCount() = dataset.size
}