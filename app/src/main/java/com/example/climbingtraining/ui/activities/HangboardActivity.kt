package com.example.climbingtraining.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ActivityHangboardBinding
import com.example.climbingtraining.model.DbResultState
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class HangboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHangboardBinding
    lateinit var viewModel: HangboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,defaultViewModelProviderFactory).get(HangboardViewModel::class.java)
        binding = ActivityHangboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hangboardNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        viewModel.dbResultStatus.observe(this){
            showResultDbToast(it)
        }

        viewModel.onViewReady()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0){
            onBackPressedDispatcher.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun showResultDbToast(status: DbResultState){
        when (status) {
            DbResultState.CONFIG_SAVE_SUCCESS -> {
                Toast.makeText(this,"The hangboard has been saved successfully.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.CONFIG_SAVE_FAILED -> {
                Toast.makeText(this,"The hangboard was not saved.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.CONFIG_EDIT_SUCCESS -> {
                Toast.makeText(this,"The hangboard has been edited successfully.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.CONFIG_EDIT_FAILED -> {
                Toast.makeText(this,"The hangboard was not edited.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.CONFIG_DELETE_SUCCESS -> {
                Toast.makeText(this,"The hangboard has been deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.CONFIG_DELETE_FAILED -> {
                Toast.makeText(this,"The hangboard has not been deleted", Toast.LENGTH_SHORT).show()
            }
            DbResultState.HISTORY_SAVE_SUCCESS -> {
                Toast.makeText(this,"Training has been saved successfully.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.HISTORY_SAVE_FAILED -> {
                Toast.makeText(this,"Training was not saved.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.HISTORY_EDIT_SUCCESS -> {
                Toast.makeText(this,"Training has been edited successfully.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.HISTORY_EDIT_FAILED -> {
                Toast.makeText(this,"Training was not edited.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.HISTORY_DELETE_SUCCESS -> {
                Toast.makeText(this,"Training has been deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            DbResultState.HISTORY_DELETE_FAILED -> {
                Toast.makeText(this,"Training has not been deleted", Toast.LENGTH_SHORT).show()
            } DbResultState.NEUTRAL -> {
                return
            }
        }
        viewModel.zeroDbStatuses()
    }

}