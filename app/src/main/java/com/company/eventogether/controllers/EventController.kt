package com.company.eventogether.controllers

import com.company.eventogether.model.EventSearch
import retrofit2.Call
import retrofit2.http.*

interface EventController {

    @GET("/search.json")
    fun retrieveEvent(@QueryMap headerMap: Map<String, String>): Call<EventSearch>
}