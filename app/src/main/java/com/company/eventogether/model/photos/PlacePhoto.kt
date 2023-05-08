package com.company.eventogether.model.photos

import com.fasterxml.jackson.annotation.JsonProperty

data class PlacePhoto(
    @JsonProperty("photos")
    val photos: List<Photo?>? = null
)

data class Photo(
    @JsonProperty("height")
    val height: Int? = null,
    @JsonProperty("html_attributions")
    val htmlAttributions: List<Any?>? = null,
    @JsonProperty("photo_reference")
    val photoReference: String? = null,
    @JsonProperty("width")
    val width: Int? = null
)