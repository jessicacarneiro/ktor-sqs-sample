package io.github.jessicacarneiro.response

import kotlinx.serialization.Serializable

@Serializable
data class QueuesResponse(
    val queues: List<String> = emptyList(),
)