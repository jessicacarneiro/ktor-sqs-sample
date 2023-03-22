package io.github.jessicacarneiro

import kotlinx.serialization.Serializable

@Serializable
data class CreateQueueRequest(
    val queueName: String,
)
