package com.company.eventogether.controllers

import com.company.eventogether.model.ReverseGeoSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface LocationController {

    @GET("maps/api/geocode/json")
    fun reverseGeoSearch(@QueryMap headerMap: Map<String, String>): Call<ReverseGeoSearch>
}