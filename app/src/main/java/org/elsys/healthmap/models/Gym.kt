package org.elsys.healthmap.models

data class Gym(
    val name: String,
    val photos: List<String>,
    val address: String,
    val rating: Float,
    val description: String,
    val tags: List<String>,
    val priceTable: List<Pair<String, String>>
)
