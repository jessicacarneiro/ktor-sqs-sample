package io.github.jessicacarneiro

import kotlinx.serialization.Serializable

@Serializable
data class SendMessagesBatchRequest(
    val messages: List<Message> = emptyList(),
)
