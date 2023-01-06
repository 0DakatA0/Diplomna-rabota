package org.elsys.healthmap.ui

data class Gym(
    val name: String,
    val photos: List<String>,
    val address: String,
    val description: String,
    val tags: List<String>,
    val priceTable: Map<String, String>
)
