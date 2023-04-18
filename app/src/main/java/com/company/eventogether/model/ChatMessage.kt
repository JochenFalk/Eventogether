package com.company.eventogether.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatMessage(
    @JsonProperty("complaint_description")
    val complaintDescription: String? = null,
    @JsonProperty("urgency")
    val urgency: Urgency? = null,
    @JsonProperty("complaint")
    val complaint: Complaint? = null
) {
    fun convertMessageToMessageDTO(): ChatMessageDTO {

        val message = this
        val urgency = HashMap<String, String>().apply {
            put("level", message.urgency?.level.toString())
            put("description", message.urgency?.description.toString())
            put("recommendation", message.urgency?.recommendation.toString())
        }
        val complaint = HashMap<String, String>().apply {
            put("category", message.complaint?.category.toString())
        }

        return ChatMessageDTO(
            complaintDescription = message.complaintDescription,
            urgency = urgency,
            complaint = complaint
        )
    }
}

data class Urgency(
    @JsonProperty("level")
    val level: String? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("recommendation")
    val recommendation: String? = null
)

data class Complaint(
    @JsonProperty("category")
    val category: String? = null
)