package com.example.xiaoyu_angelica_comp304sec001_lab04.viewmodel

import androidx.lifecycle.ViewModel
import com.example.xiaoyu_angelica_comp304sec001_lab04.model.Place
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository

class PlaceViewModel : ViewModel() {
    private val repo = PlacesRepository   // no () when repository is an object

    fun getPlaces(categoryId: String): List<com.example.xiaoyu_angelica_comp304sec001_lab04.repository.Place> = repo.getPlaces(categoryId)

    fun getPlace(placeId: String): com.example.xiaoyu_angelica_comp304sec001_lab04.repository.Place? = repo.getPlaceById(placeId)
}