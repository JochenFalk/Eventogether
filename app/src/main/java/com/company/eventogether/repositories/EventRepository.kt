package com.company.eventogether.repositories

import android.util.Log
import com.company.eventogether.BuildConfig
import com.company.eventogether.controllers.EventController
import com.company.eventogether.model.EventSearch
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository {

    private val eventController: EventController by inject(EventController::class.java)

    companion object {
        private const val TAG = "EventRepository"
    }

    fun retrieveEvents(callback: (EventSearch?) -> Unit) {

        val header = getHeaderMap()
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

    private fun getHeaderMap(): Map<String, String> {

        val map: MutableMap<String, String> = HashMap()

        map["engine"] = "google_events"
        map["q"] = "sport events in maastricht"
        map["hl"] = "nl"
        map["gl"] = "nl"
        map["api_key"] = BuildConfig.SERP_API_KEY

        return map
    }
}