package com.company.eventogether.model

import java.io.Serializable

data class EventDTO(
    val id: String? = null,
    var fbKey: String? = null,
    val name: String? = null,
    val description: String? = null,
    val location: LocationDTO? = null,
    val listGroups: ArrayList<ListGroup>? = ArrayList(),
    val reminders: ArrayList<ReminderDTO>? = ArrayList()
) : Serializable

data class LocationDTO(
    val placeId: String? = null,
    val plusCode: String? = null,
    val cityName: String? = null,
    val streetName: String? = null,
    val streetNumber: String? = null,
    val postalCode: String? = null,
    val formattedAddress: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
) : Serializable

data class ListGroup(
    var title: String? = null,
    var items: ArrayList<String>? = ArrayList()
) : Serializable

data class ReminderDTO(
    var fbKey: String? = null,
    var position: Int? = -1,
    var timeString: String? = "HH:MM",
    var isRecurring: Boolean? = true,
    var isActive: Boolean? = true
) : Serializable