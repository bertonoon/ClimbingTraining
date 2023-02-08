package com.example.climbingtraining.view

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.climbingtraining.databinding.ActivityMainBinding
import com.example.climbingtraining.model.ExerciseState
import com.example.climbingtraining.model.RunState
import com.example.climbingtraining.model.SimpleHangboard
import com.example.climbingtraining.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : MainViewModel

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->
            if (result.resultCode == Activity.RESULT_OK) {
               val hangboardConfig =
                   if ( VERSION.SDK_INT >= 33){
                       result.data?.getParcelableExtra("HangboardConfig",SimpleHangboard::class.java)
                   } else {
                       result.data?.getParcelableExtra("HangboardConfig") as SimpleHangboard?
                   }
                if (hangboardConfig != null ) viewModel.setHangboard(hangboardConfig)
            }
        }

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
        viewModel._repeatsToFinish.observe(this){
            binding.tvLeftRepeats.text = it.toString()
            viewModel.currentHangboard.value?.let { it1 ->
                setupRepeatsProgressBar(it,
                    it1.numberOfRepeats)
            }
        }
        viewModel._setsToFinish.observe(this){
            binding.tvLeftSets.text = it.toString()
            viewModel.currentHangboard.value?.let { it1 ->
                setupSetsProgressBar(it,
                    it1.numberOfSets)
            }
        }
    }

    private fun setupTimeProgressBar(timeToFinish: Long, maxTime: Long){
        binding.pbProgressBar.max = maxTime.toInt()
        binding.pbProgressBar.progress = timeToFinish.toInt()
    }

    private fun setupSetsProgressBar(setsToFinish: Int,maxSets :Int){
        binding.pbSets.max = maxSets
        binding.pbSets.progress = setsToFinish
    }
    private fun setupRepeatsProgressBar(repeatsToFinish: Int,maxRepeats :Int){
        binding.pbRepeats.max = maxRepeats
        binding.pbRepeats.progress = repeatsToFinish
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
                setupTimeProgressBar(1,1)
                setupRepeatsProgressBar(1,1)
                setupSetsProgressBar(1,1)
            }
            else -> {
                Log.e("UnknownState","Unknown state of viewModel.runState")
            }
        }
    }

    private fun onCurrentTimeToFinish(timeToFinish: Long) {
        binding.tvMainTimer.text = String.format("%.1f",timeToFinish.toFloat()/1000)
        setupTimeProgressBar(timeToFinish,
            when(viewModel.currentHangboardState.value){
                ExerciseState.INACTIVE -> 0
                ExerciseState.PREPARE -> viewModel.currentHangboard.value!!.prepareTime
                ExerciseState.HANG -> viewModel.currentHangboard.value!!.hangTime
                ExerciseState.REST -> viewModel.currentHangboard.value!!.restTime
                ExerciseState.PAUSE -> viewModel.currentHangboard.value!!.pauseTime
                null -> 0
            }
        )
    }

    private fun onCurrentHangboardChange(hangboardTimes:SimpleHangboard) {
        binding.tvHangTime.text = String.format("%.0f",hangboardTimes.hangTime.toFloat()/1000)
        binding.tvPauseTime.text = String.format("%.0f",hangboardTimes.pauseTime.toFloat()/1000)
        binding.tvRoundsToEnd.text = hangboardTimes.numberOfRepeats.toString()
        binding.tvRestTime.text = String.format("%.0f",hangboardTimes.restTime.toFloat()/1000)
        binding.tvSetsToEnd.text = hangboardTimes.numberOfSets.toString()
    }

    private fun initializeUI() {
        binding.btnStart.setOnClickListener { viewModel.onStart() }
        binding.btnStopReset.setOnClickListener { onStopResetButton() }
        binding.fabAddHangboard.setOnClickListener{ onAddHangboardButton() }

    }

    private fun onAddHangboardButton() {
        val intent = Intent(this@MainActivity,AddHangboardActivity::class.java)
        getResult.launch(intent)
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