package com.example.climbingtraining.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ActivityHangboardBinding
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

        viewModel.onViewReady()
    }
}