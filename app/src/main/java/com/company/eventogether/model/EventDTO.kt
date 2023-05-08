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
    val info: InfoDTO? = null,
    val links: LinksDTO? = null,
    val metadata: MetaDataDTO? = null,
    val followers: ArrayList<String>? = ArrayList(),
    val messages: ArrayList<ChatMessageDTO>? = ArrayList(),
    val reminders: ArrayList<ReminderDTO>? = ArrayList(),
    val listGroups: ArrayList<ListGroup>? = ArrayList(),
) : Serializable

data class InfoDTO(
    val title: String? = null,
    val description: String? = null,
    val location: LocationDTO? = null,
    val timeInMillis: Long? = null,
    val timeString: String? = null,
    val venue: Venue? = null
) : Serializable

data class LinksDTO(
    val eventLink: String? = null,
    val eventImageUrl: String? = null,
    val eventThumbnailUrl: String? = null,
    val ticketUrl: String? = null
) : Serializable

data class MetaDataDTO(
    val owner: String? = null,
    val visibility: EventVisibility? = null,
    val eventType: EventType? = null,
    val creationTimeInMillis: Long? = null
) : Serializable

data class ReminderDTO(
    var fbKey: String? = null,
    var position: Int? = -1,
    var timeString: String? = "HH:MM",
    var isRecurring: Boolean? = true,
    var isActive: Boolean? = true
) : Serializable

data class ListGroup(
    var title: String? = null,
    var items: ArrayList<Any>? = ArrayList()
) : Serializable