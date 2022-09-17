package com.rachmad.training.dicodingstoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rachmad.training.dicodingstoryapp.databinding.ActivityRegisterBinding
import com.rachmad.training.dicodingstoryapp.di.BaseActivity

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getBinding() = ActivityRegisterBinding.inflate(layoutInflater)
}