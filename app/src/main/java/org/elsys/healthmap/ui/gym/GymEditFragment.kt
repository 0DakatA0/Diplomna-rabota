package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.google.common.io.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.FragmentGymEditBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File
import java.io.FileOutputStream

class GymEditFragment : Fragment() {
    private val viewModel: GymsViewModel by activityViewModels()
    private var isChanged = false
    private val args: GymEditFragmentArgs by navArgs()
    private lateinit var gym: Gym
    private lateinit var adapter: GymImagesAdapter
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val imgName = ImagesRepository.uploadImage(uri)
                gym.photos.add(imgName)

                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val file = File(requireContext().cacheDir, imgName)
                    val outputStream = FileOutputStream(file)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
                isChanged = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymEditBinding.inflate(inflater, container, false)

        gym = viewModel.gyms.value?.get(args.id)?.let { Gym(it) }!!

        adapter = GymImagesAdapter(gym.photos, requireContext().cacheDir, viewModel.viewModelScope, isChanged)
        val gymImagesRecyclerView = binding.gymImagesRecyclerView
        gymImagesRecyclerView.adapter = adapter
        gymImagesRecyclerView.setHasFixedSize(false)

        binding.gym = gym
        binding.tags = gym.tags.joinToString(", ")

        val priceTable = binding.priceTableRecyclerView
        val priceTableDataset = gym.priceTable.toMap()
        priceTable.adapter = GymEditPriceTableAdapter(priceTableDataset)
        priceTable.setHasFixedSize(false)
        
        binding.addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                gym.description = s.toString()
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        viewModel.viewModelScope.launch {
            if (isChanged) {
                args.id?.let { GymsRepository.saveGym(it, gym) }
                isChanged = false
                args.id?.let { viewModel.saveGym(it, gym) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        context?.cacheDir?.delete()
    }
}