package io.github.jessicacarneiro.request

import io.github.jessicacarneiro.models.BatchMessage
import kotlinx.serialization.Serializable

@Serializable
data class SendMessagesBatchRequest(
    val messages: List<BatchMessage> = emptyList(),
)
