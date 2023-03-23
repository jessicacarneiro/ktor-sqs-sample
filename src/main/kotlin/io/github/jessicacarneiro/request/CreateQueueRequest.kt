package io.github.jessicacarneiro.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateQueueRequest(
    val queueName: String,
)
