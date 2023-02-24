package de.timseidel.doppelkopf.model

import java.time.LocalDateTime

data class Member(
    val id: String,
    val name: String,
    val creationTime: LocalDateTime
)