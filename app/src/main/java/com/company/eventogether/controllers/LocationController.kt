package com.company.eventogether.controllers

import com.company.eventogether.model.geosearches.GeoSearch
import com.company.eventogether.model.places.PlaceDetails
import com.company.eventogether.model.photos.PlacePhoto
import com.company.eventogether.model.geosearches.ReverseGeoSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface LocationController {

    @GET("/maps/api/geocode/json")
    fun geoSearch(@QueryMap parameters: Map<String, String>): Call<GeoSearch>

    @GET("/maps/api/geocode/json")
    fun reverseGeoSearch(@QueryMap parameters: Map<String, String>): Call<ReverseGeoSearch>

    @GET("/maps/api/place/details/json")
    fun getPlaceDetails(@QueryMap parameters: Map<String, String>): Call<PlaceDetails>

    @GET("/maps/api/place/photo")
    fun getPlacePhoto(@QueryMap parameters: Map<String, String>): Call<Any>
}