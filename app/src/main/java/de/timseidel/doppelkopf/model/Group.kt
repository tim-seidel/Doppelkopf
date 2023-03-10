package de.timseidel.doppelkopf.model

import java.time.LocalDateTime

data class Group(
    val id: String,
    val code: String,
    val date: LocalDateTime,
    var name: String
)