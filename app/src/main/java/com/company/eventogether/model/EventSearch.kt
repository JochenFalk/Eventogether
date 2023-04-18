package com.company.eventogether.model
import com.fasterxml.jackson.annotation.JsonProperty

data class EventSearch(
    @JsonProperty("events_results")
    val eventsResults: List<EventsResult?>? = null,
    @JsonProperty("search_information")
    val searchInformation: SearchInformation? = null,
    @JsonProperty("search_metadata")
    val searchMetadata: SearchMetadata? = null,
    @JsonProperty("search_parameters")
    val searchParameters: SearchParameters? = null
)

data class EventsResult(
    @JsonProperty("address")
    val address: List<String?>? = null,
    @JsonProperty("date")
    val date: Date? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("event_location_map")
    val eventLocationMap: EventLocationMap? = null,
    @JsonProperty("image")
    val image: String? = null,
    @JsonProperty("link")
    val link: String? = null,
    @JsonProperty("thumbnail")
    val thumbnail: String? = null,
    @JsonProperty("ticket_info")
    val ticketInfo: List<TicketInfo?>? = null,
    @JsonProperty("title")
    val title: String? = null,
    @JsonProperty("venue")
    val venue: Venue? = null
)

data class SearchInformation(
    @JsonProperty("events_results_state")
    val eventsResultsState: String? = null
)

data class SearchMetadata(
    @JsonProperty("created_at")
    val createdAt: String? = null,
    @JsonProperty("google_events_url")
    val googleEventsUrl: String? = null,
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("json_endpoint")
    val jsonEndpoint: String? = null,
    @JsonProperty("processed_at")
    val processedAt: String? = null,
    @JsonProperty("raw_html_file")
    val rawHtmlFile: String? = null,
    @JsonProperty("status")
    val status: String? = null,
    @JsonProperty("total_time_taken")
    val totalTimeTaken: Double? = null
)

data class SearchParameters(
    @JsonProperty("engine")
    val engine: String? = null,
    @JsonProperty("location_requested")
    val locationRequested: String? = null,
    @JsonProperty("location_used")
    val locationUsed: String? = null,
    @JsonProperty("q")
    val q: String? = null
)

data class Date(
    @JsonProperty("start_date")
    val startDate: String? = null,
    @JsonProperty("when")
    val whenX: String? = null
)

data class EventLocationMap(
    @JsonProperty("image")
    val image: String? = null,
    @JsonProperty("link")
    val link: String? = null,
    @JsonProperty("serpapi_link")
    val serpapiLink: String? = null
)

data class TicketInfo(
    @JsonProperty("link")
    val link: String? = null,
    @JsonProperty("link_type")
    val linkType: String? = null,
    @JsonProperty("source")
    val source: String? = null
)

data class Venue(
    @JsonProperty("link")
    val link: String? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("rating")
    val rating: Double? = null,
    @JsonProperty("reviews")
    val reviews: Int? = null
)