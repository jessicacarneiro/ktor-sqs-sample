package io.github.jessicacarneiro.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val body: String,
)
