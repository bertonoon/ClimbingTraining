package com.example.climbingtraining.model

import android.os.CountDownTimer
import android.util.Log
import com.example.climbingtraining.viewModel.MainViewModel


class Exercise(
    private val config: SimpleHangboard,
    private val viewModel: MainViewModel
) {

    private var timeToFinishCurrentState : Long
    private lateinit var timer :CountDownTimer
    private var currentState : ExerciseState

    private var setsToFinish : Int
    private var roundsToFinish : Int

    init {
        currentState = ExerciseState.INACTIVE
        timeToFinishCurrentState = 0

        setsToFinish = config.numberOfSets
        roundsToFinish = config.numberOfRounds

        viewModel.updateData(timeToFinishCurrentState,currentState)
    }

    private fun calculateTime(time : Long) : Boolean{

        var isFinished = false
        timer = object : CountDownTimer(time,10){
            override fun onTick(millisUntilFinished: Long) {
                timeToFinishCurrentState = millisUntilFinished
                viewModel.updateData(timeToFinishCurrentState,currentState)
            }
            override fun onFinish() {
                isFinished = true
                timeToFinishCurrentState = 0

                when(currentState){
                    ExerciseState.REST -> setsToFinish--
                    ExerciseState.PAUSE -> roundsToFinish--
                    else -> {}
                }


                nextState()
            }
        }.start()

        return isFinished
    }

    fun run(){
        nextState()
    }

    private fun nextState(){
        when(currentState){
            ExerciseState.INACTIVE -> {
                currentState = ExerciseState.PREPARE
                calculateTime(config.prepareTime)
            }
            ExerciseState.PREPARE -> {
                currentState = ExerciseState.HANG
                calculateTime(config.hangTime)
            }
            ExerciseState.HANG -> {
                if (roundsToFinish == 1 && setsToFinish == 1){
                    currentState = ExerciseState.INACTIVE
                    viewModel.onViewReady()
                } else {
                    currentState = ExerciseState.REST
                    calculateTime(config.restTime)
                }
            }
            ExerciseState.REST -> {
                if (setsToFinish > 0) {
                    currentState = ExerciseState.HANG
                    calculateTime(config.hangTime)
                } else {
                    currentState = ExerciseState.PAUSE
                    calculateTime(config.pauseTime)
                }
            }
            ExerciseState.PAUSE -> {
                if (roundsToFinish > 0) {
                    setsToFinish = config.numberOfSets
                    currentState = ExerciseState.HANG
                    calculateTime(config.hangTime)
                } else {
                    viewModel.onViewReady()
                }
            }
        }
        viewModel.updateData(timeToFinishCurrentState,currentState)
    }

    fun getState() = currentState
    fun getTimeToFinish() = timeToFinishCurrentState


    fun stop() {
        timer.cancel()
        viewModel.updateData(timeToFinishCurrentState,currentState)
    }

    fun resume() {
        calculateTime(timeToFinishCurrentState)

    }

    fun reset(){
        currentState = ExerciseState.INACTIVE
        viewModel.updateData(config.prepareTime,currentState)
    }
}