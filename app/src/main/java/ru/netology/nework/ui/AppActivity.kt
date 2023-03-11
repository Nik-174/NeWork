package ru.netology.nework.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.BuildConfig
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.ActivityAppBinding
import ru.netology.nework.databinding.ActivityAppBinding.inflate
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.MyWallPostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val wallPostViewModel: MyWallPostViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability


    lateinit var binding: ActivityAppBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("${BuildConfig.API_KEY}")
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_24)

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.posts -> {
                    findNavController(R.id.frame).navigate(R.id.feedFragment)
                }
                R.id.events -> {
                    findNavController(R.id.frame).navigate(R.id.eventFragment)
                }
            }
            binding.drawer.closeDrawer(GravityCompat.START)
            menuItem.isChecked = true
            true
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }
        lifecycleScope.launchWhenCreated {
            appAuth.authStateFlow.collectLatest {
                val textName: TextView =
                    binding.navigationView.getHeaderView(0).findViewById(R.id.nameUser)
                textName.text = it.nameUser

                Glide.with(binding.navigationView)
                    .load(it.avatarUser)
                    .error(R.drawable.ic_no_avatar_48)
                    .placeholder(R.drawable.ic_no_user_48)
                    .timeout(10_000)
                    .circleCrop()
                    .into(binding.navigationView.getHeaderView(0).findViewById(R.id.avatar))
            }
        }

        binding.navigationView.getHeaderView(0).setOnClickListener {
            findNavController(R.id.frame).navigate(R.id.myPostFragment)
            binding.drawer.closeDrawer(GravityCompat.START)
        }

        checkGoogleApiAvailability()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.drawer.isOpen
        return when (item.itemId) {
            R.id.signin -> {
                findNavController(R.id.frame).navigate(R.id.action_feedFragment_to_authenticationFragment)
                true
            }
            R.id.signup -> {
                findNavController(R.id.frame).navigate(R.id.action_feedFragment_to_registrationFragment)
                true
            }
            R.id.signout -> {
                appAuth.removeAuth()
                wallPostViewModel.removeAll()
                findNavController(R.id.frame).navigateUp()
                true
            }
            android.R.id.home -> {
                if (binding.drawer.isOpen) binding.drawer.closeDrawer(GravityCompat.START) else binding.drawer.openDrawer(
                    GravityCompat.START
                )

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }
}