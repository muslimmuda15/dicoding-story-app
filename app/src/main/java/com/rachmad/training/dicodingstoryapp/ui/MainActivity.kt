package com.rachmad.training.dicodingstoryapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rachmad.training.dicodingstoryapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    companion object {
        fun instance(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}