package com.example.xiaoyu_angelica_comp304sec001_lab04.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.xiaoyu_angelica_comp304sec001_lab04.model.Category
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository

class CategoryViewModel : ViewModel() {
    fun getCategories(context: Context): List<String> =
        PlacesRepository.getCategories(context)
}
