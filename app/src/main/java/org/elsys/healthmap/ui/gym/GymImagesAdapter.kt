package org.elsys.healthmap.ui.gym

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.StorageReference
import org.elsys.healthmap.databinding.ItemGymPictureBinding
import java.io.File
import java.security.MessageDigest

class GymImagesAdapter (
    private val dataset: List<String>,
    private val context: Context
) : RecyclerView.Adapter<GymImagesAdapter.GymPictureViewHolder>() {
    class GymPictureViewHolder(val binding: ItemGymPictureBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymPictureViewHolder {
        val binding = ItemGymPictureBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        return GymPictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymPictureViewHolder, position: Int) {
        val files = context.cacheDir.listFiles()

        files?.forEach {
            if (it.name.contains(dataset[position])) {
                Glide.with(context)
                    .load(it)
                    .into(holder.binding.root)
            }
        }
    }

    override fun getItemCount() = dataset.size
}