package com.rachmad.training.dicodingstoryapp.helper

import android.text.SpannableString
import android.util.Patterns
import android.view.View
import android.widget.TextView

fun CharSequence?.isValidEmail() = !isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()