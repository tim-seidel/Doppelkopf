package de.timseidel.doppelkopf.model

import java.time.LocalDateTime

data class DokoSession(
    val id: String,
    var name: String,
    var date: LocalDateTime,
    var tackenPrice: Double
)