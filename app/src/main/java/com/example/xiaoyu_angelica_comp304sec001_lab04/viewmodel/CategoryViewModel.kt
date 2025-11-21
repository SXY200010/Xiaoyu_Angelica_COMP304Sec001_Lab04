package com.example.xiaoyu_angelica_comp304sec001_lab04.viewmodel

import androidx.lifecycle.ViewModel
import com.example.xiaoyu_angelica_comp304sec001_lab04.model.Category
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository

class CategoryViewModel : ViewModel() {
    private val repo = PlacesRepository   // no ()

    val categories: List<String> = repo.getCategories()
}