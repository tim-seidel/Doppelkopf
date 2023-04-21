package de.timseidel.doppelkopf.util


/**
Represents a distribution of integers within a given range.
It allows the usage of negative start values by mapping the range to an array starting at 0.
@param min The minimum value in the range. Defaults to 0 if not provided.
@param max The maximum value in the range. Defaults to 0 if not provided.
 */
class RangeDistribution(val min: Int = 0, val max: Int = 0) {

    private var range = IntArray(max - min + 1) { 0 }

    /**
    Increases the count of a particular number within the range by a given amount (default is 1).
    @param number The number to increase the count of.
    @param increase The amount to increase the count by. Defaults to 1 if not provided.
     */
    fun increase(number: Int, increase: Int = 1) {
        range[number - min] += increase
    }

    /**
    Returns the count of a particular number within the range.
    @param number The number to retrieve the count of.
    @return The count of the provided number within the range.
     */
    fun get(number: Int): Int {
        return range[number - min]
    }

    /**
    Returns a list of all count values within the range.
    @return A list of all count values within the range.
     */
    fun values(): List<Int> {
        return range.asList()
    }

    /**
    Returns a list of all indices within the range.
    @return A list of all indices within the range.
     */
    fun indices(): List<Int> {
        return (min..max).toList()
    }
}