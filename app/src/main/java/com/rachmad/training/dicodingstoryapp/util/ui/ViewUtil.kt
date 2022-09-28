package com.rachmad.training.dicodingstoryapp.util.ui

import android.app.Activity
import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun View.visible(){
    visibility = View.VISIBLE
}

val View.isVisible
    get(): Boolean = visibility == View.VISIBLE

fun View.gone(){
    visibility = View.GONE
}

fun TextView.createLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(text)
    var index = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor
                textPaint.isUnderlineText = false
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        index = text.toString().indexOf(link.first, index + 1)
        spannableString.setSpan(
            clickableSpan, index, index + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not clicked
    setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
}