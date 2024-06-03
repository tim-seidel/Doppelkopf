package de.timseidel.doppelkopf.model

import java.time.LocalDateTime

data class Session(
    val id: String,
    var name: String,
    var date: LocalDateTime,
    var tackenPrice: Double,
    var members: MutableList<Member>
)