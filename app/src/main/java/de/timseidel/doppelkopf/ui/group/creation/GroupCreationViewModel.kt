package de.timseidel.doppelkopf.ui.group.creation

class GroupCreationViewModel {

    var groupName = ""
    var memberNames = mutableListOf("")
    var isBockrundeEnabled = false

    fun isValid(): Boolean {
        if (groupName.length < 3) {
            return false
        }

        val names = getFilteredMemberNames()
        if (names.size < 4) {
            return false
        }

        return names.distinct().count() == names.count()
    }

    fun getFilteredMemberNames(): List<String> {
        return memberNames.filter { name -> name.trim().isNotEmpty() }.map { name -> name.trim() }
    }
}