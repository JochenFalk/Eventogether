package com.company.eventogether.model

import java.io.Serializable

data class LocationDTO(
    var fbKey: String? = null,
    val placeId: String? = null,
    val cityName: String? = null,
    val streetName: String? = null,
    val streetNumber: String? = null,
    val postalCode: String? = null,
    val countryName: String? = null,
    val formattedAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Serializable