package com.rachmad.training.dicodingstoryapp.util.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.rachmad.training.dicodingstoryapp.R

class CustomLoading: FrameLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        isClickable = true
        isFocusable = true
        setBackgroundColor(ContextCompat.getColor(this.context, R.color.loading_background))

        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )

        val progressBar = ProgressBar(this.context).apply {
            indeterminateTintList = ContextCompat.getColorStateList(this.context, R.color.md_blue_700)
        }

        addView(progressBar, params)
    }
}