package com.company.eventogether.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.company.eventogether.model.LocationDTO
import com.company.eventogether.repositories.LocationRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.java.KoinJavaComponent.inject

class LocationViewModel(private val userProfileViewModel: UserProfileViewModel) : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var options: FirebaseRecyclerOptions<LocationDTO>

    var currentLocationObservable: MutableLiveData<LocationDTO> = MutableLiveData()
    var isLocationRequestTriggered: MutableLiveData<Boolean> = MutableLiveData()

    private val locationRepository: LocationRepository by inject(LocationRepository::class.java)

    init {
        initiateDatabaseReference()
    }

    companion object {
        private const val TAG = "LocationViewModel"
        const val LOCATION_CHILD = "location"
        const val USERS_PARENT = "users"
    }

    fun getCurrentLocation(): LocationDTO? {
        return currentLocationObservable.value
    }

    fun getGoogleLocationByAddress(address: String) {

        locationRepository.geoSearch(address) { geoSearch ->

            val result = geoSearch?.results?.get(0)

            if (result != null) {

                LocationDTO(
                    placeId = result.placeId,
                    cityName = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "locality"
                    }?.longName,
                    streetName = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "route"
                    }?.longName,
                    streetNumber = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "street_number"
                    }?.longName,
                    postalCode = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "postal_code"
                    }?.longName,
                    countryName = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "country"
                    }?.longName,
                    formattedAddress = result.formattedAddress,
                    latitude = result.geometry?.location?.lat,
                    longitude = result.geometry?.location?.lng
                )

                // Do something...
            }
        }
    }

    fun getGoogleLocationByLatLong(latLong: String) {

        locationRepository.reverseGeoSearch(latLong) { reverseGeoSearch ->

            val result = reverseGeoSearch?.results?.get(0)

            if (result != null) {

                LocationDTO(
                    placeId = result.placeId,
                    cityName = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "locality"
                    }?.longName,
                    streetName = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "route"
                    }?.longName,
                    streetNumber = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "street_number"
                    }?.longName,
                    postalCode = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "postal_code"
                    }?.longName,
                    countryName = result.addressComponents?.single { component ->
                        component?.types?.get(0) == "country"
                    }?.longName,
                    formattedAddress = result.formattedAddress,
                    latitude = result.geometry?.location?.lat,
                    longitude = result.geometry?.location?.lng
                ).also { currentLocationObservable.value = it }
            }
        }
    }

    fun updateLocation(location: LocationDTO) {

        val fbLocationKey = location.fbKey

        if (fbLocationKey != null) {

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["${LOCATION_CHILD}/$fbLocationKey"] = location

            try {
                val uid = userProfileViewModel.getCurrentUser()?.uid
                db.reference.child("/${USERS_PARENT}/$uid").updateChildren(hashMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "Location successfully updated for user uid: $uid")
                    }
            } catch (e: Exception) {
                Log.d(TAG, "A problem occurred while adding event: $e")
            }
        } else {

            try {
                val uid = userProfileViewModel.getCurrentUser()?.uid
                db.reference.child("/${USERS_PARENT}/$uid/${LOCATION_CHILD}").push().apply {
                    location.fbKey = key
                    setValue(location)
                        .addOnSuccessListener {
                            currentLocationObservable.value = location
                            Log.d(TAG, "Location successfully updated for user uid: $uid")
                        }
                }
            } catch (e: Exception) {
                Log.d(TAG, "A problem occurred while adding event: $e")
            }
        }
    }

    fun loadLastLocation(fbKey: String, callback: (LocationDTO?) -> Unit) {

        try {
            val uid = userProfileViewModel.getCurrentUser()?.uid
            db.reference.child("/${USERS_PARENT}/$uid/${LOCATION_CHILD}").child(fbKey).get()
                .addOnSuccessListener {

                    it.getValue(LocationDTO::class.java).let { location ->
                        if (location != null) {
                            callback(location)
                            Log.d(TAG, "Successfully retrieved last known location for key: $fbKey")
                        } else {
                            callback(null)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to retrieve last known location for key: $fbKey")
                }
        } catch (e: Exception) {
            Log.d(TAG, "A problem occurred while loading last known location from database: $e")
        }
    }

    private fun initiateDatabaseReference() {

        db = Firebase.database
        val uid = userProfileViewModel.getCurrentUser()?.uid
        val eventsRef = db.reference.child("/USERS_PARENT/$uid/${LOCATION_CHILD}")
        options = FirebaseRecyclerOptions.Builder<LocationDTO>()
            .setQuery(eventsRef, LocationDTO::class.java)
            .build()
    }
}