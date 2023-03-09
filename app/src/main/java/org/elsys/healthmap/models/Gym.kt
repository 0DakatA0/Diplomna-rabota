package org.elsys.healthmap.models

import com.google.firebase.firestore.GeoPoint


// FIXME Using mutable fields in data classes is generally not recommended
//  Data classes have a very handy .copy() method that allow easy modification
//  so for example if you want to add a photo you can do something like gym.copy(photos = gym.photos + "new photo")
data class Gym(
    var name: String,
    var photos: MutableList<String>,
    var coordinates: GeoPoint,
    var address: String,
    var geohash: String,
    var description: String,
    var tags: MutableList<String>,
    var priceTable: MutableMap<String, Float>,
    var owner: String
) {
    constructor() : this(
        "",
        mutableListOf(),
        GeoPoint(0.0, 0.0),
        "",
        "",
        "",
        mutableListOf(),
        mutableMapOf(),
        ""
    )

    constructor(gym: Gym) : this(
        gym.name,
        gym.photos,
        gym.coordinates,
        gym.address,
        gym.geohash,
        gym.description,
        gym.tags,
        gym.priceTable,
        gym.owner
    )
}
