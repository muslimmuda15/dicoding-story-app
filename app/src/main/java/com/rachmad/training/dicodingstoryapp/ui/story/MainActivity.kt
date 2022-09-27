package com.rachmad.training.dicodingstoryapp.ui.story

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityMainBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import com.rachmad.training.dicodingstoryapp.ui.login.LoginActivity
import com.rachmad.training.dicodingstoryapp.ui.story.add.NewStoryActivity
import com.rachmad.training.dicodingstoryapp.ui.story.detail.StoryDetailsActivity
import com.rachmad.training.dicodingstoryapp.util.LocaleHelper
import com.rachmad.training.dicodingstoryapp.util.ViewModelFactory
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MainActivity: BaseActivity<ActivityMainBinding>(), OnSelectedStory {
    /**
     * Kenapa pake shared preference padahal sudah ada data store?
     * Karena saya perlu penyimpanan data tanpa ada ikatan lifecycle dan view model
     * Untuk keperluan ganti bahasa tanpa secara langsung in-app
     */
    @Inject lateinit var sp: SharedPreferences
    private lateinit var viewModel: MainViewModel
    private lateinit var storyAdapter: StoryItemRecyclerViewAdapter
    private lateinit var alertDialog: AlertDialog

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        observer()
        listener()
    }

    private fun requestNetwork(token: String){
        viewModel.stories(token, { responseData ->
            layout.loading.gone()
            layout.refresh.isRefreshing = false
            storyAdapter.addData(responseData?.listStory)
        }, { responseData ->
            layout.loading.gone()
            layout.refresh.isRefreshing = false
            Toast.makeText(
                this@MainActivity,
                responseData?.message ?: getString(R.string.unknown_error),
                Toast.LENGTH_SHORT
            ).show()
        }, { throwable ->
            layout.loading.gone()
            layout.refresh.isRefreshing = false
            Toast.makeText(
                this@MainActivity,
                throwable?.message ?: getString(R.string.unknown_error),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun listener(){
        layout.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.getUser().token?.let {
                    requestNetwork(it)
                }
            }
        }

        layout.addStory.setOnClickListener {
            startActivity(NewStoryActivity.instance(this))
        }
    }

    private fun observer() {
        viewModel.getUserLiveData().observe(this) {
            if (!it.userId.isNullOrBlank() && !it.name.isNullOrBlank() && !it.token.isNullOrBlank()) {
                with(layout) {
                    loading.visible()
                    requestNetwork(it.token!!)
                }
            }
        }
    }

    private fun init() {
        layout.storyList.apply {
            layoutManager = LinearLayoutManager(context)
            storyAdapter = StoryItemRecyclerViewAdapter(this@MainActivity, this@MainActivity)
            adapter = storyAdapter
        }
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[MainViewModel::class.java]
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
                            viewModel.logout()
                            startActivity(LoginActivity.instance(this@MainActivity))
                            finish()
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

    override fun onClick(mainContainer: RelativeLayout, data: StoryData) {
        startActivity(StoryDetailsActivity.instance(this, data), ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            mainContainer, "story_item",
        ).toBundle())
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
        mainContainer: RelativeLayout,
        data: StoryData
    )
}