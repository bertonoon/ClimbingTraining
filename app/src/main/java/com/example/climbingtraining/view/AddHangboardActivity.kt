package com.example.climbingtraining.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.climbingtraining.databinding.ActivityAddHangboardBinding
import com.example.climbingtraining.model.SimpleHangboard
import com.example.climbingtraining.viewModel.AddHangboardViewModel

class AddHangboardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddHangboardBinding
    private lateinit var viewModel : AddHangboardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHangboardBinding.inflate(layoutInflater)
        viewModel = AddHangboardViewModel(this)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        initializeUI()
       // initializeObservers()
       // viewModel.onViewReady()
    }

    private fun initializeObservers() {
        TODO("Not yet implemented")
    }

    private fun initializeUI() {
        binding.btnSet.setOnClickListener { setHangboard() }
    }

    private fun setHangboard() {
        val hangboardConfig = SimpleHangboard(
            prepareTime = 4000L,
            hangTime = binding.etHangTime.text.toString().toLong() * 1000,
            restTime = binding.etRestTime.text.toString().toLong() * 1000,
            pauseTime = binding.etPauseTime.text.toString().toLong() * 1000,
            numberOfSets = binding.etSets.text.toString().toInt(),
            numberOfRepeats = binding.etRounds.text.toString().toInt(),
        )
        val intent = Intent(this@AddHangboardActivity,MainActivity::class.java)
        intent.putExtra("HangboardConfig",hangboardConfig)
        setResult(RESULT_OK,intent)
        finish()
    }

}