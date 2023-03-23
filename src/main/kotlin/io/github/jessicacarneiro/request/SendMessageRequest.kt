package io.github.jessicacarneiro.request

import io.github.jessicacarneiro.models.Message
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val message: Message,
)
