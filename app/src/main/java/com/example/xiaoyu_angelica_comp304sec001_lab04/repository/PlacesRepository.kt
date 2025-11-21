package com.example.xiaoyu_angelica_comp304sec001_lab04.repository

import android.content.Context
import com.example.xiaoyu_angelica_comp304sec001_lab04.R

data class Place(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

object PlacesRepository {

    fun getCategories(context: Context): List<String> = listOf(
        context.getString(R.string.category_landmarks),
        context.getString(R.string.category_museums),
        context.getString(R.string.category_cafes)
    )

    fun getPlaces(context: Context, categoryName: String): List<Place> {

        val landmarks = listOf(
            Place(
                "osaka_castle",
                context.getString(R.string.place_osaka_castle),
                context.getString(R.string.addr_osaka_castle),
                34.6873,
                135.5259
            ),
            Place(
                "dotonbori",
                context.getString(R.string.place_dotonbori),
                context.getString(R.string.addr_dotonbori),
                34.6687,
                135.5012
            ),
            Place(
                "tsutenkaku",
                context.getString(R.string.place_tsutenkaku),
                context.getString(R.string.addr_tsutenkaku),
                34.6525,
                135.5063
            )
        )

        val museums = listOf(
            Place(
                "osaka_museum_history",
                context.getString(R.string.place_osaka_museum_history),
                context.getString(R.string.addr_osaka_museum_history),
                34.6829,
                135.5206
            ),
            Place(
                "national_museum_art",
                context.getString(R.string.place_national_museum_art),
                context.getString(R.string.addr_national_museum_art),
                34.6915,
                135.4972
            ),
            Place(
                "science_museum",
                context.getString(R.string.place_science_museum),
                context.getString(R.string.addr_science_museum),
                34.6911,
                135.4979
            )
        )

        val cafes = listOf(
            Place(
                "rikuro",
                context.getString(R.string.place_rikuro),
                context.getString(R.string.addr_rikuro),
                34.6644,
                135.5019
            ),
            Place(
                "lilo",
                context.getString(R.string.place_lilo),
                context.getString(R.string.addr_lilo),
                34.6712,
                135.4999
            ),
            Place(
                "arabica",
                context.getString(R.string.place_arabica),
                context.getString(R.string.addr_arabica),
                34.6689,
                135.4938
            )
        )

        return when (categoryName) {
            context.getString(R.string.category_landmarks) -> landmarks
            context.getString(R.string.category_museums) -> museums
            context.getString(R.string.category_cafes) -> cafes
            else -> emptyList()
        }
    }

    fun getPlaceById(context: Context, placeId: String): Place? {
        val all = getCategories(context)
            .flatMap { getPlaces(context, it) }

        return all.find { it.id == placeId }
    }
}
