package io.github.jessicacarneiro

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val body: String? = ""
)
