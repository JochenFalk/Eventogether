package com.company.eventogether.model

class ChatMessageDTO(
    val sender: String? = null,
    val complaintDescription: String? = null,
    val urgency: HashMap<String, String>? = HashMap(),
    val complaint: HashMap<String, String>? = HashMap(),
    val photoUrl: String? = null,
    val imageUrl: String? = null,
)
