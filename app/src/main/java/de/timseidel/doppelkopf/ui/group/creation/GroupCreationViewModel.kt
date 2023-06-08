package de.timseidel.doppelkopf.ui.group.creation

class GroupCreationViewModel {

    var groupName = ""
    var memberNames = mutableListOf("")

    fun isValid(): Boolean {
        if (groupName.length < 3) {
            return false
        }

        val names = getFilteredMemberNames()
        return names.distinct().count() == names.count()
    }

    fun getFilteredMemberNames(): List<String> {
        return memberNames.filter { name -> name.trim().isNotEmpty() }.map { name -> name.trim() }
    }
}