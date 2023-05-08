package com.company.eventogether.repositories

import android.util.Log
import com.company.eventogether.BuildConfig
import com.company.eventogether.controllers.LocationController
import com.company.eventogether.model.geosearches.GeoSearch
import com.company.eventogether.model.geosearches.ReverseGeoSearch
import com.company.eventogether.model.places.PlaceDetails
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository {

    private val locationController: LocationController by inject(LocationController::class.java)

    companion object {
        private const val TAG = "LocationRepository"
    }

    fun geoSearch(address: String, callback: (GeoSearch?) -> Unit) {

        val parameters = getParameterMap(address = address)
        val call: Call<GeoSearch> = locationController.geoSearch(parameters)

        call.enqueue(object : Callback<GeoSearch> {
            override fun onResponse(
                call: Call<GeoSearch>,
                response: Response<GeoSearch>
            ) {

                if (response.isSuccessful) {

                    try {

                        val geoSearch = response.body() as GeoSearch
                        callback(geoSearch)

                        Log.d(TAG, "GeoSearch successfully retrieved.")

                    } catch (ex: Exception) {
                        Log.d(TAG, "Failed to retrieve GeoSearch: $ex")
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve GeoSearch: Bad response: $response")
                }
            }

            override fun onFailure(call: Call<GeoSearch>, t: Throwable) {
                Log.d(TAG, "Request failed with error: ${t.localizedMessage}")
            }
        })
    }

    fun reverseGeoSearch(latLong: String, callback: (ReverseGeoSearch?) -> Unit) {

        val parameters = getParameterMap(latLong = latLong)
        val call: Call<ReverseGeoSearch> = locationController.reverseGeoSearch(parameters)

        call.enqueue(object : Callback<ReverseGeoSearch> {
            override fun onResponse(
                call: Call<ReverseGeoSearch>,
                response: Response<ReverseGeoSearch>
            ) {

                if (response.isSuccessful) {

                    try {

                        val reverseGeoSearch = response.body() as ReverseGeoSearch
                        callback(reverseGeoSearch)

                        Log.d(TAG, "Reverse GeoSearch successfully retrieved.")

                    } catch (ex: Exception) {
                        Log.d(TAG, "Failed to retrieve reverse GeoSearch: $ex")
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve reverse GeoSearch: Bad response: $response")
                }
            }

            override fun onFailure(call: Call<ReverseGeoSearch>, t: Throwable) {
                Log.d(TAG, "Request failed with error: ${t.localizedMessage}")
            }
        })
    }

    fun getPlaceDetails(placeId: String, callback: (PlaceDetails?) -> Unit) {

        val parameters = getParameterMap(placeId = placeId)
        val call: Call<PlaceDetails> = locationController.getPlaceDetails(parameters)

        call.enqueue(object : Callback<PlaceDetails> {
            override fun onResponse(
                call: Call<PlaceDetails>,
                response: Response<PlaceDetails>
            ) {

                if (response.isSuccessful) {

                    try {

                        val placeDetails = response.body() as PlaceDetails
                        callback(placeDetails)

                        Log.d(TAG, "Place details successfully retrieved.")

                    } catch (ex: Exception) {
                        Log.d(TAG, "Failed to retrieve place details: $ex")
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve place details: Bad response: $response")
                }
            }

            override fun onFailure(call: Call<PlaceDetails>, t: Throwable) {
                Log.d(TAG, "Request failed with error: ${t.localizedMessage}")
            }
        })
    }

    fun getPlaceImage(photoRef: String, callback: (Any?) -> Unit) {

        val parameters = getParameterMap(photoRef = photoRef)
        val call: Call<Any> = locationController.getPlacePhoto(parameters)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {

                if (response.isSuccessful) {

                    try {

                        val placePhoto = response.body()
                        callback(placePhoto)

                        Log.d(TAG, "Place photo successfully retrieved.")

                    } catch (ex: Exception) {
                        Log.d(TAG, "Failed to retrieve place photo: $ex")
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve place photo: Bad response: $response")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d(TAG, "Request failed with error: ${t.localizedMessage}")
            }
        })
    }

    private fun getParameterMap(address: String? = null, latLong: String? = null, placeId: String? = null, photoRef: String? = null): Map<String, String> {

        val map: MutableMap<String, String> = HashMap()

        if (address != null) {
            map[address] = address
            map["key"] = BuildConfig.GOOGLE_GEO_API_KEY
        }
        if (latLong != null) {
            map["latlng"] = latLong
            map["key"] = BuildConfig.GOOGLE_GEO_API_KEY
        }
        if (placeId != null) {
            map["place_id"] = placeId
            map["key"] = BuildConfig.GOOGLE_PLACES_API_KEY
        }
        if (photoRef != null) {
            map["photo_reference"] = photoRef
            map["key"] = BuildConfig.GOOGLE_PLACES_API_KEY
        }

        return map
    }
}