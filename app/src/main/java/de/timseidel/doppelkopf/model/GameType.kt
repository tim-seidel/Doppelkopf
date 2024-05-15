package de.timseidel.doppelkopf.model

enum class GameType {
    NORMAL,
    SCHWARZVERLOREN,
    HOCHZEIT,
    SOLO
}

class GameTypeHelper {
    companion object {
        fun getGameTypeByString(gameTypeString: String): GameType {
            return when (gameTypeString) {
                "NORMAL" -> GameType.NORMAL
                "HOCHZEIT" -> GameType.HOCHZEIT
                "SCHWARZ VERLOREN" -> GameType.SCHWARZVERLOREN
                "SOLO" -> GameType.SOLO
                else -> throw IllegalArgumentException("Unknown game type: $gameTypeString")
            }
        }

        fun getStringByGameType(gameType: GameType): String {
            return when (gameType) {
                GameType.NORMAL -> "NORMAL"
                GameType.HOCHZEIT -> "HOCHZEIT"
                GameType.SCHWARZVERLOREN -> "SCHWARZ VERLOREN"
                GameType.SOLO -> "SOLO"
            }
        }

        fun getGameTypeByInt(gameTypeInt: Int): GameType {
            return when (gameTypeInt) {
                0 -> GameType.NORMAL
                1 -> GameType.HOCHZEIT
                2 -> GameType.SCHWARZVERLOREN
                3 -> GameType.SOLO
                else -> throw IllegalArgumentException("Unknown game type: $gameTypeInt")
            }
        }

        fun getIntByGameType(gameType: GameType): Int {
            return when (gameType) {
                GameType.NORMAL -> 0
                GameType.HOCHZEIT -> 1
                GameType.SCHWARZVERLOREN -> 2
                GameType.SOLO -> 3
            }
        }

        fun getStringList(): List<String> {
            return listOf("NORMAL", "HOCHZEIT", "SCHWARZ VERLOREN", "SOLO")
        }

        fun getGameTypeList(): List<GameType> {
            return listOf(GameType.NORMAL, GameType.SCHWARZVERLOREN, GameType.HOCHZEIT, GameType.SOLO)
        }
    }
}