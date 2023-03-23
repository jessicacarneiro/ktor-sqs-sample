package io.github.jessicacarneiro

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Message(
    val id: String? = UUID.randomUUID().toString(),
    val body: String,
)
