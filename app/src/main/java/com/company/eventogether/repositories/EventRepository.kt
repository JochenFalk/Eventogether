package com.company.eventogether.repositories

import android.util.Log
import com.company.eventogether.BuildConfig
import com.company.eventogether.controllers.EventController
import com.company.eventogether.model.EventSearch
import com.company.eventogether.model.LocationDTO
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class EventRepository {

    private val eventController: EventController by inject(EventController::class.java)

    companion object {
        private const val TAG = "EventRepository"
        private const val GOOGLE_EVENTS = "google_events"
    }

    fun retrieveEvents(type: String, location: LocationDTO, callback: (EventSearch?) -> Unit) {

        val header = getHeaderMap(type, location)
        val call: Call<EventSearch> = eventController.retrieveEvent(header)

        call.enqueue(object : Callback<EventSearch> {
            override fun onResponse(call: Call<EventSearch>, response: Response<EventSearch>) {

                if (response.isSuccessful) {

                    try {

                        val eventSearch = response.body() as EventSearch
                        callback(eventSearch)

                        Log.d(
                            TAG,
                            "Event search successfully retrieved. Events returned: ${eventSearch.eventsResults?.size}"
                        )

                    } catch (ex: Exception) {
                        Log.d(TAG, "Failed to retrieve event search: $ex")
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve event search: Bad response: $response")
                }
            }

            override fun onFailure(call: Call<EventSearch>, t: Throwable) {
                Log.d(TAG, "Request failed with error: ${t.localizedMessage}")
            }
        })
    }

    private fun getHeaderMap(type: String, location: LocationDTO): Map<String, String> {

        val map: MutableMap<String, String> = HashMap()

        map["engine"] = GOOGLE_EVENTS
        map["q"] = "$type events in ${location.cityName}"
        map["hl"] = Locale.getDefault().language.lowercase()
        map["gl"] = Locale.getDefault().country.lowercase()
        map["api_key"] = BuildConfig.SERP_API_KEY

        return map
    }
}