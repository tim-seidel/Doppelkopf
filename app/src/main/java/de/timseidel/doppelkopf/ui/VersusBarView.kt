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

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_versus_bar, this)

        pb_versus = findViewById(R.id.pb_game_points)
        tv_left = findViewById(R.id.tv_game_score_pb_left)
        tv_right = findViewById(R.id.tv_game_score_pb_right)

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

    private lateinit var tv_left: TextView
    private lateinit var tv_right: TextView
    private lateinit var pb_versus: ProgressBar

    fun setMin(value: Int) {
        pb_versus.min = value
    }

    fun setMax(value: Int) {
        pb_versus.max = value
    }

    fun setProgress(value: Int) {
        pb_versus.progress = value
    }

    fun setLeftText(text: String) {
        tv_left.text = text
    }

    fun setRightText(text: String) {
        tv_right.text = text
    }
}