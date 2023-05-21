package de.timseidel.doppelkopf.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R

class VersusBarView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView
    private lateinit var pbVersus: ProgressBar

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_versus_bar, this)
        findViews()

        setAttributes(attrs)
    }

    private fun findViews() {
        pbVersus = findViewById(R.id.pb_game_points)
        tvLeft = findViewById(R.id.tv_game_points_pb_left)
        tvRight = findViewById(R.id.tv_game_points_pb_right)
    }

    private fun setAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.VersusBarView)
        try {
            val min = ta.getInteger(R.styleable.VersusBarView_min, 0)
            val max = ta.getInteger(R.styleable.VersusBarView_max, 100)
            setMin(min)
            setMax(max)
            setProgress(ta.getInteger(R.styleable.VersusBarView_progress, 0))
            setLeftText(ta.getString(R.styleable.VersusBarView_leftText) ?: min.toString())
            setRightText(ta.getString(R.styleable.VersusBarView_leftText) ?: max.toString())
        } finally {
            ta.recycle()
        }
    }

    fun setMin(value: Int) {
        pbVersus.min = value
    }

    fun setMax(value: Int) {
        pbVersus.max = value
    }

    fun setProgress(value: Int) {
        pbVersus.progress = value
    }

    fun setLeftText(text: String) {
        tvLeft.text = text
    }

    fun setRightText(text: String) {
        tvRight.text = text
    }
}