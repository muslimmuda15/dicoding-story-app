package com.rachmad.training.dicodingstoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityLoginBinding
import com.rachmad.training.dicodingstoryapp.di.BaseActivity
import com.rachmad.training.dicodingstoryapp.helper.ui.createLinks
import com.rachmad.training.dicodingstoryapp.helper.ui.hideKeyboard

class LoginActivity: BaseActivity<ActivityLoginBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout.signup.createLinks(Pair(getString(R.string.sign_up), View.OnClickListener {
            hideKeyboard(this)
            startActivity(RegisterActivity.instance(this))
        }))
    }

    override fun getBinding() = ActivityLoginBinding.inflate(layoutInflater)
}