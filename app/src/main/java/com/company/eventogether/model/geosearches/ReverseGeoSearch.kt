package com.company.eventogether.model.geosearches

import com.fasterxml.jackson.annotation.JsonProperty

data class ReverseGeoSearch(
    @JsonProperty("results")
    val results: List<Result?>? = null
)