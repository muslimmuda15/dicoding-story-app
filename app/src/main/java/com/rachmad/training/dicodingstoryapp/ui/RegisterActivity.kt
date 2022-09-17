package com.rachmad.training.dicodingstoryapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityRegisterBinding
import com.rachmad.training.dicodingstoryapp.di.BaseActivity
import com.rachmad.training.dicodingstoryapp.helper.ui.createLinks

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        layout.signin.createLinks(Pair(getString(R.string.sign_in), View.OnClickListener {
            finish()
        }))
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