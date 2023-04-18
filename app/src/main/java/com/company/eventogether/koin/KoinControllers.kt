package com.company.eventogether.koin

import org.koin.dsl.module
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.company.eventogether.AppSettings
import com.company.eventogether.controllers.EventController
import com.company.eventogether.controllers.LocationController
import com.company.eventogether.repositories.LocationRepository
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

val koinControllers = module {

    single {
        val mapper = ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        Retrofit.Builder()
            .baseUrl(AppSettings.SERP_API_BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(EventController::class.java)
    }

    single {
        val mapper = ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        Retrofit.Builder()
            .baseUrl(AppSettings.GOOGLE_GEO_URL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(LocationController::class.java)
    }
}