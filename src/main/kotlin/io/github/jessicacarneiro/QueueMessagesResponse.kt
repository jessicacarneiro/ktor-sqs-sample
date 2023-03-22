package io.github.jessicacarneiro

import kotlinx.serialization.Serializable

@Serializable
data class QueueMessagesResponse(
    val messages: List<QueueMessage> = emptyList(),
)

@Serializable
data class QueueMessage(
    val body: String?,
)
