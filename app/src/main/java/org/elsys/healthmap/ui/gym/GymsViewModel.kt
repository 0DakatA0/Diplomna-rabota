package org.elsys.healthmap.ui.gym

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay
import org.elsys.healthmap.ui.Gym

class GymsViewModel : ViewModel() {
    val gyms: LiveData<List<Gym>> = liveData {
        emit(
            listOf(
                Gym(
                    "Gym1",
                    emptyList(),
                    "address1",
                    3.5f,
                    "This id description",
                    listOf("tag1", "tag2"),
                    mapOf(
                        Pair("Product", "Price"),
                        Pair("Product", "Price")
                    )
                ),
                Gym(
                    "Gym1",
                    emptyList(),
                    "address1",
                    4f,
                    "This id description",
                    listOf("tag1", "tag2"),
                    mapOf(
                        Pair("Product", "Price"),
                        Pair("Product", "Price")
                    )
                ),
                Gym(
                    "Gym1",
                    emptyList(),
                    "address1",
                    5f,
                    "This id description",
                    listOf("tag1", "tag2"),
                    mapOf(
                        Pair("Product", "Price"),
                        Pair("Product", "Price")
                    )
                ),
            )
        )

        delay(5000)

        emit(
            listOf(
                Gym(
                    "Gym1",
                    emptyList(),
                    "address2",
                    3.5f,
                    "This id description",
                    listOf("tag1", "tag2"),
                    mapOf(
                        Pair("Product", "Price"),
                        Pair("Product", "Price")
                    )
                ),
                Gym(
                    "Gym1",
                    emptyList(),
                    "address2",
                    4f,
                    "This id description",
                    listOf("tag1", "tag2"),
                    mapOf(
                        Pair("Product", "Price"),
                        Pair("Product", "Price")
                    )
                ),
                Gym(
                    "Gym1",
                    emptyList(),
                    "address2",
                    5f,
                    "This id description",
                    listOf("tag1", "tag2"),
                    mapOf(
                        Pair("Product", "Price"),
                        Pair("Product", "Price")
                    )
                ),
            )
        )
    }
}