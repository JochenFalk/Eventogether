package com.company.eventogether

object AppSettings {

    const val SERP_API_BASE_URL = "https://serpapi.com"
    const val GOOGLE_GEO_URL = "https://maps.googleapis.com"
    const val PRIVACY_URL = "https://"
    const val TOS_URL = "https://"

    const val EVENT_UPDATE_PERIOD_IN_MILLIS = 10*86400000 // 86400000 == 24 hours
    val TICKET_PROVIDERS = ArrayList<String>().apply {
        add("Spotify")
        add("TicketSwap")
        add("Bandsintown.com")
        add("Seatsnet.com")
    }
}