package com.company.eventogether.koin

import com.company.eventogether.viewmodels.ChatMessageViewModel
import com.company.eventogether.viewmodels.EventViewModel
import com.company.eventogether.viewmodels.LocationViewModel
import com.company.eventogether.viewmodels.UserProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinViews = module {

    viewModel {
       UserProfileViewModel()
    }
    viewModel {
        ChatMessageViewModel(get())
    }
    viewModel {
        EventViewModel(get())
    }
    viewModel {
        LocationViewModel()
    }
}