package com.rachmad.training.dicodingstoryapp.di

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B: ViewBinding>: AppCompatActivity() {
    abstract fun getBinding(): B
    private lateinit var bind: B
    private var _layout: B? = null
    protected val layout: B
        get() = _layout ?: throw IllegalStateException("The activity not active or destroyed")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = getBinding()
        _layout = bind
        setContentView(bind.root)
    }
}