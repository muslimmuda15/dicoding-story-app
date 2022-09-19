package com.rachmad.training.dicodingstoryapp.util.ui

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rachmad.training.dicodingstoryapp.R

class CustomPasswordEditText: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(isFocused){
            if(text.toString().length < 6){
                error = context.getString(R.string.password_min_message)
            }
        }
    }

    private fun init(context: Context) {
        backgroundTintList = (ContextCompat.getColorStateList(context, R.color.white))
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    }
}