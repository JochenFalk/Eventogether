package com.company.eventogether.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.eventogether.model.LocationDTO
import com.company.eventogether.repositories.LocationRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import org.koin.java.KoinJavaComponent

class LocationViewModel : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var options: FirebaseRecyclerOptions<LocationDTO>

    var isLocationRequestTriggered: MutableLiveData<Boolean> = MutableLiveData()

    private val locationRepository: LocationRepository by KoinJavaComponent.inject(
        LocationRepository::class.java
    )

    init {
        isLocationRequestTriggered.value = true
    }

    companion object {
        private const val TAG = "LocationViewModel"
    }

    fun getGoogleLocation(fusedLocation: Location) {

        val latLong = "${fusedLocation.latitude}, ${fusedLocation.longitude}"
        locationRepository.reverseGeoSearch(latLong) { reverseGeoSearch ->



        }
    }
}