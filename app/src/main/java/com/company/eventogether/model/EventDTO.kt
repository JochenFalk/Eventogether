package com.company.eventogether.model

import java.io.Serializable

enum class EventType {
    All,
    SPORT,
    SOCIAL,
    DANCING,
    OUTDOOR,
    INDOOR
}

enum class EventVisibility {
    PUBLIC,
    PRIVATE,
    INVITE_ONLY
}

data class EventDTO(
    var fbKey: String? = null,
    val info: EventInfoDTO? = null,
    val metadata: EventMetaDataDTO? = null,
    val followers: ArrayList<String>? = ArrayList(),
    val messages: ArrayList<ChatMessageDTO>? = ArrayList(),
    val reminders: ArrayList<EventReminderDTO>? = ArrayList(),
    var expandableLists: ArrayList<EventExpandableGroup>? = ArrayList(),
) : Serializable

data class EventInfoDTO(
    val title: String? = null,
    val description: String? = null,
    val timeInMillis: Long? = null,
    val timeString: String? = null,
    val address: List<String?>? = null,
    val ticketInfo: List<TicketInfo?>? = null,
    val venue: EventVenue? = null,
    val thumbnailUrl: String? = null,
    val links: EventLinks? = null
) : Serializable

data class EventVenue(
    val name: String? = null,
    val rating: Double? = null,
    val reviews: Int? = null,
    val thumbnailUrl: String? = null
) : Serializable

data class EventLinks(
    val eventLink: String? = null,
    val eventMapsUrl: String? = null,
    val eventMapsThumbnailUrl: String? = null
) : Serializable

data class EventMetaDataDTO(
    val owner: String? = null,
    val visibility: EventVisibility? = null,
    val eventType: EventType? = null,
    val creationTimeInMillis: Long? = null
) : Serializable

data class EventReminderDTO(
    var fbKey: String? = null,
    var position: Int? = -1,
    var timeString: String? = "HH:MM",
    var isRecurring: Boolean? = true,
    var isActive: Boolean? = true
) : Serializable

data class EventExpandableGroup(
    var groupTitle: String? = null,
    var groupItems: ArrayList<Any>? = null
) : Serializable