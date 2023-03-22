package de.timseidel.doppelkopf.ui.util

import android.content.Context
import android.util.DisplayMetrics

class Converter {
    companion object {
        fun convertDpToPixels(dp: Float, context: Context): Int {
            return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        }

        fun convertPixelsToDp(px: Int, context: Context) : Float{
            return px/context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        }
    }
}