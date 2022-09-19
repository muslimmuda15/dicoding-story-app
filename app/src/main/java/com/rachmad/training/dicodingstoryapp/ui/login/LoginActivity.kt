package com.rachmad.training.dicodingstoryapp.ui.login

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityLoginBinding
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.model.LoginRequestData
import com.rachmad.training.dicodingstoryapp.util.ViewModelFactory
import com.rachmad.training.dicodingstoryapp.util.ui.createLinks
import com.rachmad.training.dicodingstoryapp.util.ui.hideKeyboard
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import com.rachmad.training.dicodingstoryapp.ui.MainActivity
import com.rachmad.training.dicodingstoryapp.ui.register.RegisterActivity
import com.rachmad.training.dicodingstoryapp.util.isValidEmail
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class LoginActivity: BaseActivity<ActivityLoginBinding>() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        listener()
        observer()
    }

    private fun observer(){
        viewModel.getUser().observe(this) {
            if (!it.userId.isNullOrBlank() && !it.name.isNullOrBlank() && !it.token.isNullOrBlank()) {
                startActivity(MainActivity.instance(this))
            }
        }
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[LoginViewModel::class.java]
    }

    private fun listener(){
        with(layout){
            signup.createLinks(Pair(getString(R.string.sign_up), View.OnClickListener {
                hideKeyboard(this@LoginActivity)
                startActivity(RegisterActivity.instance(this@LoginActivity))
            }))

            login.setOnClickListener {
                val email = email.text.toString()
                val password = password.text.toString()
                if(email.isBlank())
                    emailLayout.error = getString(R.string.empty_email)
                else if(!email.isValidEmail()){
                    emailLayout.error = getString(R.string.email_not_valid)
                }
                if(password.isBlank()) {
                    passwordLayout.error = getString(R.string.empty_password)
                } else if(password.length < 6){
                    passwordLayout.error = getString(R.string.password_min_message)
                }

                if(email.isNotBlank() && password.isNotBlank() && email.isValidEmail() && password.length >= 6){
                    loading.visible()
                    viewModel.login(LoginRequestData(email = email, password = password), {
                        loading.gone()
                        it?.loginResult?.let { loginData ->
                            viewModel.saveUser(loginData)
                            startActivity(MainActivity.instance(this@LoginActivity))
                        }
                    }, {
                        loading.gone()
                        Toast.makeText(this@LoginActivity, it?.message ?: getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                    }, {
                        loading.gone()
                        Toast.makeText(this@LoginActivity, it?.message ?: getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }

    override fun getBinding() = ActivityLoginBinding.inflate(layoutInflater)
}