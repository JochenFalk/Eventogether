package com.company.eventogether.model.geosearches

import com.fasterxml.jackson.annotation.JsonProperty

data class GeoSearch(
    @JsonProperty("results")
    val results: List<Result?>? = null,
    @JsonProperty("status")
    val status: String? = null
)

data class Result(
    @JsonProperty("address_components")
    val addressComponents: List<AddressComponent?>? = null,
    @JsonProperty("formatted_address")
    val formattedAddress: String? = null,
    @JsonProperty("geometry")
    val geometry: Geometry? = null,
    @JsonProperty("place_id")
    val placeId: String? = null,
    @JsonProperty("plus_code")
    val plusCode: PlusCode? = null,
    @JsonProperty("types")
    val types: List<String?>? = null
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
    @JsonProperty("location_type")
    val locationType: String? = null,
    @JsonProperty("viewport")
    val viewport: Viewport? = null
)

data class PlusCode(
    @JsonProperty("compound_code")
    val compoundCode: String? = null,
    @JsonProperty("global_code")
    val globalCode: String? = null
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