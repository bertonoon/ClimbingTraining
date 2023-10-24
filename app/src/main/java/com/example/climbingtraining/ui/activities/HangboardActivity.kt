package com.example.climbingtraining.ui.activities

import android.Manifest
import android.app.AlertDialog.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ActivityHangboardBinding
import com.example.climbingtraining.models.DbResultState
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import com.example.climbingtraining.utils.Constants
import kotlinx.android.synthetic.main.activity_hangboard.*


class HangboardActivity : AppCompatActivity() {


    private lateinit var binding: ActivityHangboardBinding
    lateinit var viewModel: HangboardViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    private val broadCastStartReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.onStart()
        }
    }
    private val broadCastStopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.onStop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        viewModel = ViewModelProvider(
            this,
            defaultViewModelProviderFactory
        )[HangboardViewModel::class.java]
        binding = ActivityHangboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupMenu()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hangboardNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        viewModel.dbResultStatus.observe(this) {
            showResultDbToast(it)
        }
        viewModel.onViewReady()
        requestPermissions()

        registerReceiver(broadCastStartReceiver, IntentFilter(Constants.RECEIVER_START_TIMER))
        registerReceiver(broadCastStopReceiver, IntentFilter(Constants.RECEIVER_STOP_TIMER))
    }

    private fun setupMenu() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.palette2)


        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> Toast.makeText(this, "Clicked item 1", Toast.LENGTH_SHORT).show()
                R.id.miItem2 -> Toast.makeText(this, "Clicked item 2", Toast.LENGTH_SHORT).show()
                R.id.miItem3 -> Toast.makeText(this, "Clicked item 3", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        //supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        val tvTitle: TextView? = findViewById<TextView>(R.id.tvTitle)
        tvTitle?.text = "TEST"
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCastStartReceiver)
        unregisterReceiver(broadCastStopReceiver)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }


    private fun hasNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun hasForegroundServicePermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (!hasForegroundServicePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE)
            }
        }



        if (permissionsToRequest.isNotEmpty()) {
            var showDialog = false

            if (permissionsToRequest.contains(Manifest.permission.FOREGROUND_SERVICE)) {
                showDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.FOREGROUND_SERVICE
                    )
                } else {
                    true
                }
            }

            if (permissionsToRequest.contains(Manifest.permission.POST_NOTIFICATIONS)) {
                showDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    true
                }
            }
            if (!showDialog) {
                val builder = AlertDialog.Builder(this@HangboardActivity)
                builder
                    .setTitle(getString(R.string.notificationRequestTitle))
                    .setMessage(getString(R.string.notificationRequestMessage))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.notificationRequestButtonText))
                    { dialog, _ ->
                        dialog.dismiss()
                        ActivityCompat.requestPermissions(
                            this@HangboardActivity,
                            permissionsToRequest.toTypedArray(),
                            Constants.REQUEST_PERMISSION_CODE
                        )
                    }
                builder.create().show()
            }
        }
    }

    private fun showResultDbToast(status: DbResultState) {
        when (status) {
            DbResultState.CONFIG_SAVE_SUCCESS -> Toast.makeText(
                this,
                "The hangboard has been saved successfully.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.CONFIG_SAVE_FAILED -> Toast.makeText(
                this,
                "The hangboard was not saved.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.CONFIG_EDIT_SUCCESS -> Toast.makeText(
                this,
                "The hangboard has been edited successfully.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.CONFIG_EDIT_FAILED -> Toast.makeText(
                this,
                "The hangboard was not edited.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.CONFIG_DELETE_SUCCESS -> Toast.makeText(
                this,
                "The hangboard has been deleted successfully.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.CONFIG_DELETE_FAILED -> Toast.makeText(
                this,
                "The hangboard has not been deleted",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_SAVE_SUCCESS -> Toast.makeText(
                this,
                "Training has been saved successfully.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_SAVE_FAILED -> Toast.makeText(
                this,
                "Training was not saved.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_EDIT_SUCCESS -> Toast.makeText(
                this,
                "Training has been edited successfully.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_EDIT_FAILED -> Toast.makeText(
                this,
                "Training was not edited.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_DELETE_SUCCESS -> Toast.makeText(
                this,
                "Training has been deleted successfully.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_DELETE_FAILED -> Toast.makeText(
                this,
                "Training has not been deleted.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.NEUTRAL -> return
            DbResultState.CONFIG_FETCH_FAILED -> Toast.makeText(
                this,
                "Error reading configurations.",
                Toast.LENGTH_SHORT
            ).show()
            DbResultState.HISTORY_FETCH_FAILED -> Toast.makeText(
                this,
                "Error reading history.",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewModel.zeroDbStatuses()
    }


}