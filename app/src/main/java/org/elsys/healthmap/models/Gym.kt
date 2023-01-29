package org.elsys.healthmap.models

data class Gym(
    var name: String,
    var photos: MutableList<String>,
    var address: String,
    var rating: Float,
    var description: String,
    var tags: MutableList<String>,
    var priceTable: Map<String, Float>
) {
    constructor() : this("", mutableListOf(), "", 0f, "", mutableListOf(), emptyMap())
    constructor(gym: Gym) : this(
        gym.name,
        gym.photos,
        gym.address,
        gym.rating,
        gym.description,
        gym.tags,
        gym.priceTable
    )
}
