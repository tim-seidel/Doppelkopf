package de.timseidel.doppelkopf.util

class IdGenerator {
    companion object{
        fun generateIdWithTimestamp(objectTag: String = "id"): String{
            return "${objectTag}_${System.currentTimeMillis()}"
        }
    }
}