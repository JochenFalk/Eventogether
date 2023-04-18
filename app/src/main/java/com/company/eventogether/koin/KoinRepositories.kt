package com.company.eventogether.koin

import com.company.eventogether.repositories.EventRepository
import com.company.eventogether.repositories.LocationRepository
import org.koin.dsl.module

val koinRepositories = module {

    single {
        EventRepository()
    }

    single {
        LocationRepository()
    }
}