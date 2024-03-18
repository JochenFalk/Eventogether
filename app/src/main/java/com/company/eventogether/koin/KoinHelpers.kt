package com.company.eventogether.koin

import com.company.eventogether.helpclasses.StringResourcesProvider
import com.company.eventogether.adapters.ExpandableListAdapter
import com.company.eventogether.helpclasses.SharedPreferencesHelper
import com.company.eventogether.helpclasses.reminders.RemindersManager
import org.koin.dsl.module

val koinHelpers = module {

    single {
        SharedPreferencesHelper
    }
    single {
        StringResourcesProvider(get())
    }

    single {
        RemindersManager()
    }
}

val koinExpandableListAdapter = module {

    factory { (callback: (String) -> Unit) ->
        ExpandableListAdapter(callback)
    }
}