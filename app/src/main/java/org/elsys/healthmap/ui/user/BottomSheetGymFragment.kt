package org.elsys.healthmap.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.elsys.healthmap.databinding.FragmentBottomsheetGymBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.ui.viewmodels.UserViewModel

class BottomSheetGymFragment(
    private val gym: Gym,
    private val viewModel: UserViewModel
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomsheetGymBinding.inflate(inflater, container, false)
        binding.gym = gym

//        val bottomSheet = binding.bottomSheetView
//        val sheetBehavior: BottomSheetBehavior<ConstraintLayout> = BottomSheetBehavior.from(bottomSheet)
//        sheetBehavior.isFitToContents = true // the default
//        sheetBehavior.peekHeight = 200

        val imagesAdapter = GymImagesBottomSheetAdapter(
            viewModel.viewModelScope,
            requireContext().cacheDir,
            gym.photos
        )

        binding.gymImagesRecyclerViewBottomSheet.adapter = imagesAdapter
        binding.gymImagesRecyclerViewBottomSheet.setHasFixedSize(true)

        val priceTableAdapter = PriceTableBottomSheetAdapter(gym.priceTable)

        binding.priceTableRecyclerViewBottomSheet.adapter = priceTableAdapter
        binding.priceTableRecyclerViewBottomSheet.setHasFixedSize(true)

        return binding.root
    }
}