package com.rachmad.training.dicodingstoryapp.helper.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rachmad.training.dicodingstoryapp.R
import javax.inject.Inject

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
                setError("Password must be at least 6 characters")
            }
        }
    }

    private fun init(context: Context) {
        backgroundTintList = (ContextCompat.getColorStateList(context, R.color.white))
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    }
}