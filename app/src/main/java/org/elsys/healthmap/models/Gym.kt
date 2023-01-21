package org.elsys.healthmap.models

data class Gym(
    val name: String,
    val photos: List<String>,
    val address: String,
    val rating: Float,
    var description: String,
    val tags: List<String>,
    val priceTable: Map<String, Float>
) {
    constructor() : this("", emptyList(), "", 0f, "", emptyList(), emptyMap())
}
