package com.example.climbingtraining.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.climbingtraining.databinding.ActivityMainBinding
import com.example.climbingtraining.model.ExerciseState
import com.example.climbingtraining.model.RunState
import com.example.climbingtraining.model.SimpleHangboard
import com.example.climbingtraining.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = MainViewModel(this)
        supportActionBar?.hide()
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        initializeUI()
        initializeObservers()
        viewModel.onViewReady()
    }

    private fun initializeObservers() {
        viewModel.currentHangboard.observe(this){onCurrentHangboardChange(it)}
        viewModel.currentTimeToFinish.observe(this){onCurrentTimeToFinish(it)}
        viewModel.currentHangboardState.observe(this){
            if (viewModel.runState.value != RunState.UNINITIALIZED ) {
                binding.tvCurrentState.visibility = View.VISIBLE
                if ( it == ExerciseState.INACTIVE )
                    binding.tvCurrentState.text = "WAITING FOR START"
                else
                    binding.tvCurrentState.text = it.toString()
            } else
                binding.tvCurrentState.visibility = View.INVISIBLE

        }
        viewModel.runState.observe(this){
            onRunStateChange()
        }
    }

    private fun setupProgressBar(timeToFinish: Long,maxTime: Long){
        binding.pbProgressBar.max = maxTime.toInt()
        binding.pbProgressBar.progress = timeToFinish.toInt()
    }

    private fun onRunStateChange() {
        when (viewModel.runState.value) {
            RunState.ACTIVE -> {
                binding.btnStopReset.text = "STOP"
            }
            RunState.STOPPED -> {
                binding.btnStopReset.text = "RESET"
            }
            RunState.INITIALIZED -> {
                binding.btnStopReset.text = "STOP"
                setupProgressBar(1,1)
            }
            else -> {
                Log.e("UnknownState","Unknown state of viewModel.runState")
            }
        }
    }

    private fun onCurrentTimeToFinish(timeToFinish: Long) {
        binding.tvMainTimer.text = String.format("%.2f",timeToFinish.toFloat()/1000)
        setupProgressBar(timeToFinish,
            when(viewModel.currentHangboardState.value){
                ExerciseState.INACTIVE -> viewModel.currentHangboard.value!!.prepareTime
                ExerciseState.PREPARE -> viewModel.currentHangboard.value!!.prepareTime
                ExerciseState.HANG -> viewModel.currentHangboard.value!!.hangTime
                ExerciseState.REST -> viewModel.currentHangboard.value!!.restTime
                ExerciseState.PAUSE -> viewModel.currentHangboard.value!!.pauseTime
                else -> 0
            }
        )
    }

    private fun onCurrentHangboardChange(hangboardTimes:SimpleHangboard) {
        binding.tvHangTime.text = String.format("%.2f",hangboardTimes.hangTime.toFloat()/1000)
        binding.tvPauseTime.text = String.format("%.2f",hangboardTimes.pauseTime.toFloat()/1000)
        binding.tvRoundsToEnd.text = hangboardTimes.numberOfRounds.toString()
        binding.tvRestTime.text = String.format("%.2f",hangboardTimes.restTime.toFloat()/1000)
        binding.tvSetsToEnd.text = hangboardTimes.numberOfSets.toString()
    }

    private fun initializeUI() {
        binding.btnStart.setOnClickListener { viewModel.onStart() }
        binding.btnStopReset.setOnClickListener { onStopResetButton() }

    }

    private fun onStopResetButton(){
        when (viewModel.runState.value) {
            RunState.ACTIVE -> {
                viewModel.onStop()
            }
            RunState.STOPPED -> {
                viewModel.onReset()
            }
            else -> {
                Log.e("UnknownState","Unknown state of viewModel.runState")
            }
        }
    }

}