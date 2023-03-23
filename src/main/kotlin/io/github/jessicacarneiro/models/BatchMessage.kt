package io.github.jessicacarneiro.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class BatchMessage(
    val id: String = UUID.randomUUID().toString(),
    val body: String,
)
