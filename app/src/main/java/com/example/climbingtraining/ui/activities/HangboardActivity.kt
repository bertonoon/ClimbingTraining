package com.example.climbingtraining.ui.activities

import android.Manifest
import android.app.AlertDialog.*
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ActivityHangboardBinding
import com.example.climbingtraining.models.DbResultState
import com.example.climbingtraining.models.ExerciseState
import com.example.climbingtraining.models.RunState
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import com.example.climbingtraining.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HangboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHangboardBinding
    lateinit var viewModel: HangboardViewModel
    private var notificationBuilder : NotificationCompat.Builder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            defaultViewModelProviderFactory
        )[HangboardViewModel::class.java]
        binding = ActivityHangboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.hangboardNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        viewModel.dbResultStatus.observe(this) {
            showResultDbToast(it)
        }
        viewModel.onViewReady()
        requestPermissions()

        if (notificationBuilder == null){
            initNotification()
        }

        var oldSecondsValue = 0
        viewModel.secondsToFinish.observe(this@HangboardActivity) {
            if (it != oldSecondsValue && viewModel.currentHangboardState.value != ExerciseState.INACTIVE) {
                lifecycleScope.launch(Dispatchers.Main) {
                    with(NotificationManagerCompat.from(this@HangboardActivity)) {
                        notificationBuilder!!.setContentText(
                            "Time to finish: $it s")
                        notificationBuilder!!.setContentTitle(viewModel.currentHangboardState.value.toString())
                        notify(1, notificationBuilder!!.build())
                        oldSecondsValue = it
                    }
                }
            }
        }
        viewModel.runState.observe(this@HangboardActivity){
            if ( it == RunState.INITIALIZED) {
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(1)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0){
            onBackPressedDispatcher.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun initNotification() {
//        val intent = Intent(this@HangboardActivity, HangboardActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val flag =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                PendingIntent.FLAG_IMMUTABLE
//            else
//                0
//
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, flag)

        notificationBuilder = NotificationCompat.Builder(this@HangboardActivity, Constants.NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_baseline_watch_later_24)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
          //setContentIntent(pendingIntent)
            setAutoCancel(false)
            setOngoing(true)
            setDefaults(0)
            setVibrate(null)
            setSound(null)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

    }

    private fun hasNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    private fun requestPermissions(){
        val permissionsToRequest = mutableListOf<String>()
        if(!hasNotificationPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (permissionsToRequest.isNotEmpty()){
            if (permissionsToRequest.contains(Manifest.permission.POST_NOTIFICATIONS)) {
                val showDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    true
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
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////        if (requestCode == Constants.REQUEST_PERMISSION_CODE && grantResults.isNotEmpty()){
////            for(i in grantResults.indices){
////                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
////                    Log.d("PermissionsRequest","${permissions[i]} granted.")
////                }
////            }
////        }
//    }
    private fun showResultDbToast(status: DbResultState){
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
            else -> return
        }
        viewModel.zeroDbStatuses()
    }

}