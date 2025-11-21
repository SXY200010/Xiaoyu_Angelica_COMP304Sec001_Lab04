package com.example.xiaoyu_angelica_comp304sec001_lab04.repository

data class Place(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

object PlacesRepository {

    private val categories = listOf("Landmarks", "Museums", "Cafes")

    private val places = mapOf(
        "Landmarks" to listOf(
            Place("osaka_castle", "Osaka Castle", "1-1 Osakajo, Chuo Ward, Osaka", 34.6873, 135.5259),
            Place("dotonbori", "Dotonbori", "Chuo Ward, Osaka", 34.6687, 135.5012),
            Place("tsutenkaku", "Tsutenkaku Tower", "1-18-6 Ebisuhigashi, Naniwa Ward, Osaka", 34.6525, 135.5063)
        ),
        "Museums" to listOf(
            Place("osaka_museum_history", "Osaka Museum of History", "4-1-32 Otemae, Chuo Ward, Osaka", 34.6829, 135.5206),
            Place("national_museum_art", "National Museum of Art, Osaka", "4-2-55 Nakanoshima, Kita Ward, Osaka", 34.6915, 135.4972),
            Place("science_museum", "Osaka Science Museum", "4-2-1 Nakanoshima, Kita Ward, Osaka", 34.6911, 135.4979)
        ),
        "Cafes" to listOf(
            Place("rikuro", "Rikuro’s Cheesecake Cafe", "3-2-12 Namba, Chuo Ward, Osaka", 34.6644, 135.5019),
            Place("lilo", "LiLo Coffee Roasters", "1-10-28 Nishishinsaibashi, Chuo Ward, Osaka", 34.6712, 135.4999),
            Place("arabica", "Arabica Osaka", "1-6-5 Minamihorie, Nishi Ward, Osaka", 34.6689, 135.4938)
        )
    )

    // Public API used by ViewModels and Activities
    fun getCategories(): List<String> = categories

    fun getPlaces(categoryId: String): List<Place> =
        places[categoryId] ?: emptyList()

    fun getPlaceById(placeId: String): Place? =
        places.values.flatten().find { it.id == placeId }
}