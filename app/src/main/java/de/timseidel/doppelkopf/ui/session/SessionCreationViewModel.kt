package de.timseidel.doppelkopf.ui.session

class SessionCreationViewModel {

    var sessionName = "Doppelkopfabend"

    var player1Name = ""
    var player2Name = ""
    var player3Name = ""
    var player4Name = ""
    var player5Name = ""
    var player6Name = ""

    fun checkIsSetupValid(): Boolean {
        return sessionName.isNotEmpty() and (getPlayerCount() >= 4) && !isKeyPlayerMissing()
    }

    fun getPlayerCount(): Int {
        var c = 0
        if (player1Name.isNotEmpty()) c++
        if (player2Name.isNotEmpty()) c++
        if (player3Name.isNotEmpty()) c++
        if (player4Name.isNotEmpty()) c++
        if (player5Name.isNotEmpty()) c++
        if (player6Name.isNotEmpty()) c++

        return c
    }

    fun isKeyPlayerMissing(): Boolean {
        if (player1Name.isEmpty()) return true
        if (player2Name.isEmpty()) return true
        if (player3Name.isEmpty()) return true
        if (player4Name.isEmpty()) return true
        return false
    }

    fun playerNamesAsList(): List<String> {
        val names = mutableListOf(
            player1Name,
            player2Name,
            player3Name,
            player4Name
        )
        if (player5Name.isNotEmpty()) names.add(player5Name)
        if (player6Name.isNotEmpty()) names.add(player6Name)

        return names
    }
}