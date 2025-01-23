package de.timseidel.doppelkopf.ui.group

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import de.timseidel.doppelkopf.R


class MemberSelectView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private lateinit var cbMemberSelect: CheckBox

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_member_select, this)

        findViews()
        parseAttributes(attrs)
    }

    private fun findViews() {
        cbMemberSelect = findViewById(R.id.cb_member_select)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MemberSelectView)
        try {
            val name = ta.getString(R.styleable.MemberSelectView_memberName) ?: ""
            val isChecked = ta.getBoolean(R.styleable.MemberSelectView_isMemberChecked, false)

            setName(name)
            setChecked(isChecked)
        } finally {
            ta.recycle()
        }
    }

    fun setIsCheckedListener(listener: CompoundButton.OnCheckedChangeListener) {
        cbMemberSelect.setOnCheckedChangeListener(listener)
    }

    fun setName(name: String) {
        cbMemberSelect.text = name
    }

    fun setChecked(isChecked: Boolean) {
        cbMemberSelect.isChecked = isChecked
    }
}