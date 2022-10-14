package com.rachmad.training.dicodingstoryapp.ui.story

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityMainBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.ui.login.LoginActivity
import com.rachmad.training.dicodingstoryapp.ui.story.add.NewStoryActivity
import com.rachmad.training.dicodingstoryapp.ui.story.detail.StoryDetailsActivity
import com.rachmad.training.dicodingstoryapp.util.LocaleHelper
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainActivity: BaseActivity<ActivityMainBinding>(), OnSelectedStory {
    @Inject lateinit var sp: SharedPreferences
    private val viewModel: MainViewModel by viewModels()
    private lateinit var storyAdapter: StoryItemRecyclerViewAdapter
    private lateinit var alertDialog: AlertDialog

    private lateinit var activityResult: ActivityResultLauncher<Intent>
    private var isAuth = true

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        initResult()
        observer()
        listener()
    }

    private fun initResult(){
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                NewStoryActivity.RESULT_REQUEST_STORY -> {
                    val isSuccess = result.data?.getBooleanExtra(NewStoryActivity.IS_STORY_SUCCESS, false) ?: false
                    if(isSuccess) {
                        if(!viewModel.getToken.isNullOrBlank()) {
                            requestNetwork()
                        } else {
                            isAuth = false
                            viewModel.logout()
                        }
                    }
                }
            }
        }
    }

    private fun requestNetwork(){
        viewModel.stories()?.let { stories ->
            Timber.d("STORIES IS FOUND", stories)
            stories.removeObservers(this)
            stories.observe(this) {
                it.map {
                    Timber.d("STORIES DATA : " + it.toString())
                }
                storyAdapter.submitData(lifecycle, it)
            }
        } ?: run {
            Timber.d("STORIES IS EMPTY")
        }
    }

    private fun listener(){
        layout.refresh.setOnRefreshListener {
            if(!viewModel.getToken.isNullOrBlank()) {
                requestNetwork()
            } else {
                isAuth = false
                viewModel.logout()
            }
        }

        layout.addStory.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            activityResult.launch(NewStoryActivity.instance(this), options)
        }
    }

    private fun observer() {
        /**
         * Authentication
         * Request network when login data is found
         * Logout when account data is empty
         */
        viewModel.getUserLiveData().observe(this) {
            it?.let {
                if (it.userId.isNotBlank() && !it.name.isNullOrBlank() && !it.token.isNullOrBlank()) {
                    with(layout) {
                        isAuth = true
//                        loading.visible()
                        requestNetwork()
                    }
                }
            } ?: run {
                if(isAuth){
                    Toast.makeText(this, getString(R.string.not_auth_message), Toast.LENGTH_SHORT).show()
                }
                startActivity(LoginActivity.instance(this@MainActivity))
                finish()
            }
        }
    }

    private fun init() {
        layout.storyList.apply {
            layoutManager = LinearLayoutManager(context)
            storyAdapter = StoryItemRecyclerViewAdapter(this@MainActivity, this@MainActivity)
            adapter = storyAdapter
                .withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storyAdapter.retry()
                    }
                )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                alertDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_description))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        lifecycleScope.launch {
                            isAuth = false
                            viewModel.logout()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        alertDialog.dismiss()
                    }
                    .show()
            }
            R.id.english -> {
                reloadActivity("en")
            }
            R.id.indonesia -> {
                reloadActivity("id")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(options: ActivityOptionsCompat, data: StoryData) {
        startActivity(
            StoryDetailsActivity.instance(this, data),
            options.toBundle())
    }

    private fun reloadActivity(lang: String){
        sp.edit().putString(LocaleHelper.LOCALE_CODE, lang).apply()
        App.LANGUAGE = lang
        recreate()
    }

    companion object {
        fun instance(context: Context): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun getBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun attachBaseContext(newBase: Context) {
        App.LANGUAGE = sp.getString(LocaleHelper.LOCALE_CODE, "en") ?: "en"
        super.attachBaseContext(LocaleHelper.applyLanguageContext(newBase, Locale(App.LANGUAGE)))
    }
}

interface OnSelectedStory{
    fun onClick(
        options: ActivityOptionsCompat,
        data: StoryData
    )
}