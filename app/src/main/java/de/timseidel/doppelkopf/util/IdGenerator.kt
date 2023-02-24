package de.timseidel.doppelkopf.util

class IdGenerator {
    companion object {
        fun generateIdWithTimestamp(objectTag: String = "id"): String {
            return "${objectTag}_${System.currentTimeMillis()}"
        }

        fun generateGroupCode(): String {
            val codeBuilder = StringBuilder()
            val millisString = (System.currentTimeMillis() % 1000000).toString()
            while (codeBuilder.length + millisString.length < 6) {
                codeBuilder.append("0")
            }
            codeBuilder.append(millisString)

            return codeBuilder.toString()
        }
    }
}