package io.github.jessicacarneiro

import kotlinx.serialization.Serializable

@Serializable
data class QueuesResponse(
    val queues: List<String> = emptyList(),
)