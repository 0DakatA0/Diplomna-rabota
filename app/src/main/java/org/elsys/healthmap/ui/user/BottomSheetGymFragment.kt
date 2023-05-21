package org.elsys.healthmap.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.elsys.healthmap.databinding.FragmentBottomsheetGymBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.ui.gym.GymEditPriceTableAdapter
import org.elsys.healthmap.ui.gym.GymImagesAdapter
import org.elsys.healthmap.ui.viewmodels.BottomSheetViewModel
import org.elsys.healthmap.ui.viewmodels.ImageFailureViewModel
import org.elsys.healthmap.ui.viewmodels.UserViewModel

class BottomSheetGymFragment(
    private val gym: Gym
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ModalBottomSheet"
    }
    private val viewModel: BottomSheetViewModel by viewModels()
    private val imageFailureViewModel: ImageFailureViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomsheetGymBinding.inflate(inflater, container, false)
        binding.gym = gym

        viewModel.gym = gym

        val photos = viewModel.photos
        val priceTable = viewModel.priceTable

        val imagesAdapter = GymImagesAdapter(
            photos,
            requireContext().cacheDir,
            viewModel.viewModelScope,
            {
                imageFailureViewModel.addFailedImage(it)
            }
        ) {}

        imageFailureViewModel.hasImageDownloadFailed.observe(viewLifecycleOwner) {
            if (it) {
                imageFailureViewModel.showSnackBarMessage(
                    binding.root,
                    binding.root,
                    imagesAdapter
                )
            }
        }

        binding.gymImagesRecyclerViewBottomSheet.adapter = imagesAdapter
        binding.gymImagesRecyclerViewBottomSheet.setHasFixedSize(true)

        val priceTableAdapter = GymEditPriceTableAdapter(priceTable) {}

        binding.priceTableRecyclerViewBottomSheet.adapter = priceTableAdapter
        binding.priceTableRecyclerViewBottomSheet.setHasFixedSize(true)

        return binding.root
    }
}