package com.company.eventogether.model.places

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaceDetails(
    @JsonProperty("html_attributions")
    val htmlAttributions: List<Any?>? = null,
    @JsonProperty("result")
    val result: Result? = null,
    @JsonProperty("status")
    val status: String? = null
)

data class Result(
    @JsonProperty("address_components")
    val addressComponents: List<AddressComponent?>? = null,
    @JsonProperty("adr_address")
    val adrAddress: String? = null,
    @JsonProperty("business_status")
    val businessStatus: String? = null,
    @JsonProperty("formatted_address")
    val formattedAddress: String? = null,
    @JsonProperty("formatted_phone_number")
    val formattedPhoneNumber: String? = null,
    @JsonProperty("geometry")
    val geometry: Geometry? = null,
    @JsonProperty("icon")
    val icon: String? = null,
    @JsonProperty("icon_background_color")
    val iconBackgroundColor: String? = null,
    @JsonProperty("icon_mask_base_uri")
    val iconMaskBaseUri: String? = null,
    @JsonProperty("international_phone_number")
    val internationalPhoneNumber: String? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("opening_hours")
    val openingHours: OpeningHours? = null,
    @JsonProperty("photos")
    val photos: List<Photo?>? = null,
    @JsonProperty("place_id")
    val placeId: String? = null,
    @JsonProperty("plus_code")
    val plusCode: PlusCode? = null,
    @JsonProperty("rating")
    val rating: Int? = null,
    @JsonProperty("reference")
    val reference: String? = null,
    @JsonProperty("reviews")
    val reviews: List<Review?>? = null,
    @JsonProperty("types")
    val types: List<String?>? = null,
    @JsonProperty("url")
    val url: String? = null,
    @JsonProperty("user_ratings_total")
    val userRatingsTotal: Int? = null,
    @JsonProperty("utc_offset")
    val utcOffset: Int? = null,
    @JsonProperty("vicinity")
    val vicinity: String? = null,
    @JsonProperty("website")
    val website: String? = null
)

data class AddressComponent(
    @JsonProperty("long_name")
    val longName: String? = null,
    @JsonProperty("short_name")
    val shortName: String? = null,
    @JsonProperty("types")
    val types: List<String?>? = null
)

data class Geometry(
    @JsonProperty("location")
    val location: Location? = null,
    @JsonProperty("viewport")
    val viewport: Viewport? = null
)

data class OpeningHours(
    @JsonProperty("open_now")
    val openNow: Boolean? = null,
    @JsonProperty("periods")
    val periods: List<Period?>? = null,
    @JsonProperty("weekday_text")
    val weekdayText: List<String?>? = null
)

data class Photo(
    @JsonProperty("height")
    val height: Int? = null,
    @JsonProperty("html_attributions")
    val htmlAttributions: List<String?>? = null,
    @JsonProperty("photo_reference")
    val photoReference: String? = null,
    @JsonProperty("width")
    val width: Int? = null
)

data class PlusCode(
    @JsonProperty("compound_code")
    val compoundCode: String? = null,
    @JsonProperty("global_code")
    val globalCode: String? = null
)

data class Review(
    @JsonProperty("author_name")
    val authorName: String? = null,
    @JsonProperty("author_url")
    val authorUrl: String? = null,
    @JsonProperty("language")
    val language: String? = null,
    @JsonProperty("profile_photo_url")
    val profilePhotoUrl: String? = null,
    @JsonProperty("rating")
    val rating: Int? = null,
    @JsonProperty("relative_time_description")
    val relativeTimeDescription: String? = null,
    @JsonProperty("text")
    val text: String? = null,
    @JsonProperty("time")
    val time: Int? = null
)

data class Location(
    @JsonProperty("lat")
    val lat: Double? = null,
    @JsonProperty("lng")
    val lng: Double? = null
)

data class Viewport(
    @JsonProperty("northeast")
    val northeast: Northeast? = null,
    @JsonProperty("southwest")
    val southwest: Southwest? = null
)

data class Northeast(
    @JsonProperty("lat")
    val lat: Double? = null,
    @JsonProperty("lng")
    val lng: Double? = null
)

data class Southwest(
    @JsonProperty("lat")
    val lat: Double? = null,
    @JsonProperty("lng")
    val lng: Double? = null
)

data class Period(
    @JsonProperty("close")
    val close: Close? = null,
    @JsonProperty("open")
    val `open`: Open? = null
)

data class Close(
    @JsonProperty("day")
    val day: Int? = null,
    @JsonProperty("time")
    val time: String? = null
)

data class Open(
    @JsonProperty("day")
    val day: Int? = null,
    @JsonProperty("time")
    val time: String? = null
)