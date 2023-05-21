package org.elsys.healthmap.ui.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.elsys.healthmap.R

class ImageFailureViewModel : ViewModel() {
    private val _hasImageDownloadFailed = MutableLiveData(false)
    val hasImageDownloadFailed: LiveData<Boolean>
        get() = _hasImageDownloadFailed

    private var failedImagePositions: List<Int> = emptyList()

    fun addFailedImage(position: Int) {
        failedImagePositions = failedImagePositions.plus(position)

        if(_hasImageDownloadFailed.value == false){
            _hasImageDownloadFailed.value = true
        }
    }

    fun clearFailedImages() {
        failedImagePositions = listOf()

        if(_hasImageDownloadFailed.value == true){
            _hasImageDownloadFailed.value = false
        }
    }

    fun showSnackBarMessage(rootView: View, anchorView: View, adapter: RecyclerView.Adapter<*>) {
        Snackbar.make(
            rootView,
            R.string.snackbar_unable_to_download,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.retry) {
                if(failedImagePositions.isNullOrEmpty()) {
                    return@setAction
                } else {
                    for (imagePosition in failedImagePositions) {
                        adapter.notifyItemChanged(imagePosition)
                    }

                    clearFailedImages()
                }
            }
            .setAnchorView(anchorView)
            .show()
    }
}