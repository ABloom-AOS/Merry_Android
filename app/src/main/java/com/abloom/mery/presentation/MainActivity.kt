package com.abloom.mery.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.abloom.mery.R
import com.abloom.mery.databinding.ActivityMainBinding
import com.abloom.mery.presentation.common.extension.showToast
import com.abloom.mery.presentation.ui.home.HomeFragmentDirections
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.mainScreen) as NavHostFragment
    }

    private val navController: NavController by lazy { navHostFragment.navController }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupWindowInsetsListener()
        setupBackPressedDispatcher()
        setupDestinationChangedListener()

        askNotificationPermission()
        logFirebaseToken()
        backgroundPush()
    }

    private fun logFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener {
            Log.e("TAG", "Token $it")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
        }
    }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback { handleBackPressed() }
    }

    private fun handleBackPressed() {
        val destination = navController.currentDestination ?: return
        if (destination.id == R.id.homeFragment) finishSoftly() else navController.navigateUp()
    }

    private fun finishSoftly() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < ASK_AGAIN_EXIT_DURATION) {
            finish()
        } else {
            backPressedTime = currentTime
            showToast(R.string.app_finish_confirm_message)
        }
    }

    // TODO("추후 애니메이션으로 배경이 서서히 변하도록 수정할 예정")
    private fun setupDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.homeFragment) {
                binding.root.background = getColor(R.color.primary_5).toDrawable()
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars = true
            } else {
                binding.root.background = Color.BLACK.toDrawable()
                WindowCompat.getInsetsController(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars = false
            }
        }
    }

    private fun backgroundPush() {
        //TODO(백그라운 처리)
        val intent = intent
        intent.getStringExtra("qid")?.let {
            val questionId = it.toLong()
            val action =
                HomeFragmentDirections.actionHomeFragmentToQnaFragment(questionId)
            navController.navigate(action)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val newConfiguration = Configuration(newBase.resources.configuration)
        newConfiguration.fontScale = FIXED_FONT_SCALE
        newConfiguration.densityDpi = DisplayMetrics.DENSITY_420
        applyOverrideConfiguration(newConfiguration)
        super.attachBaseContext(newBase)
    }

    companion object {

        private const val ASK_AGAIN_EXIT_DURATION = 2_000
        private const val FIXED_FONT_SCALE = 1.0f
    }
}

