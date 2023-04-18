package com.company.eventogether.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.company.eventogether.helpclasses.StringResourcesProvider
import com.company.eventogether.R
import org.koin.java.KoinJavaComponent.inject

data class Event(
    @JsonProperty("event_name")
    val eventName: String? = null,
    @JsonProperty("event_id")
    val eventId: String? = null,
    @JsonProperty("event_description")
    val eventDescription: String? = null,
    @JsonProperty("eventInfo1")
    val eventInfo1: EventInfo1? = null,
    @JsonProperty("eventInfo2")
    val eventInfo2: EventInfo2? = null,
    @JsonProperty("eventInfo3")
    val eventInfo3: List<EventInfo3?>? = null
) {
    fun convertToEventDTO(): EventDTO {

        val stringResourcesProvider: StringResourcesProvider by inject(StringResourcesProvider::class.java)

        val listGroups = ArrayList<ListGroup>().apply {

            val group1 = ListGroup().apply {
                val list = ArrayList<String>()
                title = stringResourcesProvider.getString(R.string.event_information1)
                eventInfo1?.let { item ->
                    list.add("$item")
                }
                items?.add(list.toString())
            }

            val group2 = ListGroup().apply {
                val list = ArrayList<String>()
                title = stringResourcesProvider.getString(R.string.event_information2)
                eventInfo2?.let { item ->
                    list.add("$item")
                }
                items?.add(list.toString())
            }

            val group3 = ListGroup().apply {
                var count = 1
                val list = ArrayList<String>()
                title = stringResourcesProvider.getString(R.string.event_information3)
                eventInfo3?.forEach { item ->
                    if (item != null) {
                        list.add("$count: $item")
                        count++
                    }
                }
                items?.add(list.toString())
            }

            this.add(group1)
            this.add(group2)
            this.add(group3)
        }

        return EventDTO(
            id = this.eventId,
            name = this.eventName,
            description = this.eventDescription,
            location = null,
            listGroups = listGroups
        )
    }
}

data class EventInfo1(
    @JsonProperty("location")
    val location: String? = null
)

data class EventInfo2(
    @JsonProperty("time")
    val time: String? = null
)

data class EventInfo3(
    @JsonProperty("route")
    val route: String? = null,
    @JsonProperty("regions")
    val regions: String? = null
)