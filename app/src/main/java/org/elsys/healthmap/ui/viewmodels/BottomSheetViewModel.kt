package org.elsys.healthmap.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.elsys.healthmap.models.Gym

class BottomSheetViewModel : ViewModel() {
    private val _photos = MutableLiveData<List<String>>()
    val photos: LiveData<List<String>>
        get() = _photos

    private val _priceTable = MutableLiveData<Map<String, Float>>()
    val priceTable: LiveData<Map<String, Float>>
        get() = _priceTable

    var gym: Gym? = null
        set(value) {
            _photos.value = value?.photos
            _priceTable.value = value?.priceTable
            field = value
        }
}