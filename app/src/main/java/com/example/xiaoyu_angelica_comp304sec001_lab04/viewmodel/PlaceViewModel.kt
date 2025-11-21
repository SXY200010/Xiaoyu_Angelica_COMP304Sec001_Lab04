package com.example.xiaoyu_angelica_comp304sec001_lab04.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.xiaoyu_angelica_comp304sec001_lab04.model.Place
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository

class PlaceViewModel : ViewModel() {
    fun getPlaces(context: Context, categoryName: String) =
        PlacesRepository.getPlaces(context, categoryName)

    fun getPlace(context: Context, placeId: String) =
        PlacesRepository.getPlaceById(context, placeId)
}
