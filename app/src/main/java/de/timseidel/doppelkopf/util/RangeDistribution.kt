package de.timseidel.doppelkopf.util

class RangeDistribution(val min: Int = 0, val max: Int = 0) {
    private var range = IntArray(max-min+1){0}

    fun increase(number: Int, increase: Int = 1){
        range[number-min] += increase
    }

    fun get(number: Int): Int{
        return range[number-min]
    }

    fun values(): List<Int>{
        return range.asList()
    }

    fun indices(): List<Int>{
        return (min..max).toList()
    }
}