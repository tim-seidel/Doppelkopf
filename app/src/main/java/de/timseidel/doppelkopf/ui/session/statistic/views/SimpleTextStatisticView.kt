package de.timseidel.doppelkopf.ui.session.statistic.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R

class SimpleTextStatisticView constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_simple_text_statistic, this)

        tvTitle = findViewById(R.id.tv_textstat_title)
        tvDescription = findViewById(R.id.tv_textstat_description)
        tvValue = findViewById(R.id.tv_textstat_value)
        ivIcon = findViewById(R.id.iv_textstat_icon)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleTextStatisticView)
        try {
            setTitle(ta.getString(R.styleable.SimpleTextStatisticView_title) ?: "Statistik")
            setDescription(ta.getString(R.styleable.SimpleTextStatisticView_description) ?: "")
            setValue(ta.getString(R.styleable.SimpleTextStatisticView_value) ?: "?")
        } finally {
            ta.recycle()
        }
    }

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvValue: TextView
    private lateinit var ivIcon: ImageView

    fun setTitle(title: String){
        tvTitle.text = title
    }

    fun setDescription(description: String){
        tvDescription.text = description
    }

    fun setValue(value: String){
        tvValue.text = value
    }

    fun setIcon(icon: Drawable){
        ivIcon.setImageDrawable(icon)
    }
}