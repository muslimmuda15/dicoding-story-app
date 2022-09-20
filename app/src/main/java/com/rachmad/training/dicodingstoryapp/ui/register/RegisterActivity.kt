package com.rachmad.training.dicodingstoryapp.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityRegisterBinding
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.model.LoginRequestData
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import com.rachmad.training.dicodingstoryapp.ui.login.LoginViewModel
import com.rachmad.training.dicodingstoryapp.util.ViewModelFactory
import com.rachmad.training.dicodingstoryapp.util.isValidEmail
import com.rachmad.training.dicodingstoryapp.util.ui.createLinks
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        initView()
        listener()
    }

    private fun init(){
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[RegisterViewModel::class.java]
    }

    private fun initView(){
        setSupportActionBar(layout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
    }

    private fun listener(){
        with(layout) {
            signin.createLinks(Pair(getString(R.string.sign_in), View.OnClickListener {
                finish()
            }))

            signup.setOnClickListener {
                val fullName = fullName.text.toString()
                val email = email.text.toString()
                val password = password.text.toString()
                val confirmPassword = confirmPassword.text.toString()

                if(fullName.isBlank()) {
                    fullNameLayout.error = getString(R.string.empty_name)
                }

                if(email.isBlank()) {
                    emailLayout.error = getString(R.string.empty_email)
                } else if(!email.isValidEmail()){
                    emailLayout.error = getString(R.string.email_not_valid)
                }

                if(password.isBlank()) {
                    passwordLayout.error = getString(R.string.empty_password)
                } else if(password.length < 6){
                    passwordLayout.error = getString(R.string.password_min_message)
                }

                if(confirmPassword.isBlank()) {
                    confirmPasswordLayout.error = getString(R.string.empty_password)
                } else if(confirmPassword != password){
                    confirmPasswordLayout.error = getString(R.string.password_not_match)
                }

                if(fullName.isNotBlank() &&
                    email.isNotBlank() &&
                    email.isValidEmail() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    password.length >= 6 &&
                    password == confirmPassword){

                    loading.visible()
                    viewModel.register(LoginRequestData(name = fullName, email = email, password = password), {
                        loading.gone()
                        Toast.makeText(this@RegisterActivity, it?.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                        loading.gone()
                        Toast.makeText(this@RegisterActivity, it?.message ?: getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                    }, {
                        loading.gone()
                        Toast.makeText(this@RegisterActivity, it?.message ?: getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getBinding() = ActivityRegisterBinding.inflate(layoutInflater)

    companion object {
        fun instance(context: Context): Intent = Intent(context, RegisterActivity::class.java)
    }
}