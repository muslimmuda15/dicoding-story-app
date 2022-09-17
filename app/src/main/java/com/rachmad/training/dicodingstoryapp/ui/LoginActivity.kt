package com.rachmad.training.dicodingstoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rachmad.training.dicodingstoryapp.databinding.ActivityLoginBinding
import com.rachmad.training.dicodingstoryapp.di.BaseActivity

class LoginActivity: BaseActivity<ActivityLoginBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getBinding() = ActivityLoginBinding.inflate(layoutInflater)
}