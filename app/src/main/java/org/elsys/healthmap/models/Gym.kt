package org.elsys.healthmap.models

data class Gym(
    var name: String,
    var photos: List<String>,
    var address: String,
    var rating: Float,
    var description: String,
    var tags: List<String>,
    var priceTable: Map<String, Float>
) {
    constructor() : this("", emptyList(), "", 0f, "", emptyList(), emptyMap())
    constructor(gym: Gym) : this(gym.name, gym.photos, gym.address, gym.rating, gym.description, gym.tags, gym.priceTable)
}
