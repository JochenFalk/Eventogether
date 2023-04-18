package com.company.eventogether.repositories

import android.util.Log
import com.company.eventogether.BuildConfig
import com.company.eventogether.controllers.LocationController
import com.company.eventogether.model.ReverseGeoSearch
import org.koin.java.KoinJavaComponent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository {

    private val locationController: LocationController by KoinJavaComponent.inject(
        LocationController::class.java
    )

    companion object {
        private const val TAG = "LocationRepository"
    }

    fun reverseGeoSearch(latLong: String, callback: (ReverseGeoSearch?) -> Unit) {

        val header = getHeaderMap(latLong)
        val call: Call<ReverseGeoSearch> = locationController.reverseGeoSearch(header)

        call.enqueue(object : Callback<ReverseGeoSearch> {
            override fun onResponse(
                call: Call<ReverseGeoSearch>,
                response: Response<ReverseGeoSearch>
            ) {

                if (response.isSuccessful) {

                    try {

                        val reverseGeoSearch = response.body() as ReverseGeoSearch
                        callback(reverseGeoSearch)

                        Log.d(
                            TAG,
                            "Reverse GeoSearch successfully retrieved. Events returned: $reverseGeoSearch"
                        )

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

    private fun getHeaderMap(latLong: String): Map<String, String> {

        val map: MutableMap<String, String> = HashMap()

        map["latlng"] = latLong
        map["key"] = BuildConfig.GOOGLE_GEO_API_KEY

        return map
    }
}