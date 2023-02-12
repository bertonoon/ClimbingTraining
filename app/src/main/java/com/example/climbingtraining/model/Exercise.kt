package com.example.climbingtraining.model

import android.os.CountDownTimer
import com.example.climbingtraining.ui.viewModels.HangboardViewModel


class Exercise(
    private val viewModel: HangboardViewModel
) {

    private var timeToFinishCurrentState : Long
    private lateinit var timer :CountDownTimer
    private var currentState : ExerciseState


    private var config : SimpleHangboard = BasicHangboardTimes.getBasicTimes() // TODO Zmienić na ostatnie używane
    private var setsToFinish : Int = config.numberOfSets
    private var repeatsToFinish : Int = config.numberOfRepeats

    init {
        currentState = ExerciseState.INACTIVE
        timeToFinishCurrentState = 0
        viewModel.updateData(
            timeToFinishCurrentState,
            currentState,
            setsToFinish,
            repeatsToFinish)
    }

    fun setHangboard(newConfig : SimpleHangboard){
        config = newConfig
        setsToFinish = config.numberOfSets
        repeatsToFinish = config.numberOfRepeats
    }
    fun getHangboard() = config

    private fun calculateTime(time : Long){
        timer = object : CountDownTimer(time,10){
            override fun onTick(millisUntilFinished: Long) {
                timeToFinishCurrentState = millisUntilFinished
                viewModel.updateData(
                    timeToFinishCurrentState,
                    currentState,
                    setsToFinish,
                    repeatsToFinish)
            }
            override fun onFinish() {
                timeToFinishCurrentState = 0
                when(currentState){
                    ExerciseState.REST -> repeatsToFinish--
                    ExerciseState.PAUSE -> setsToFinish--
                    ExerciseState.HANG -> {
                        if ( repeatsToFinish < 2 && setsToFinish < 2) {
                            setsToFinish = 0
                            repeatsToFinish = 0
                            viewModel.onFinish()
                        }
                    }
                    else -> {}
                }
                if (setsToFinish > 0) {
                    nextState()
                }
            }
        }.start()
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
                currentState = ExerciseState.REST
                calculateTime(config.restTime)
            }
            ExerciseState.REST -> {
                if (repeatsToFinish > 0) {
                    currentState = ExerciseState.HANG
                    calculateTime(config.hangTime)
                } else {
                    currentState = ExerciseState.PAUSE
                    calculateTime(config.pauseTime)
                }
            }
            ExerciseState.PAUSE -> {
                if (setsToFinish > 0) {
                    repeatsToFinish = config.numberOfRepeats
                    currentState = ExerciseState.HANG
                    calculateTime(config.hangTime)
                }
            }
        }
        viewModel.updateData(
            timeToFinishCurrentState,
            currentState,
            setsToFinish,
            repeatsToFinish)
    }

    fun getState() = currentState
    fun getTimeToFinish() = timeToFinishCurrentState


    fun stop() {
        timer.cancel()
        viewModel.updateData(timeToFinishCurrentState,currentState,setsToFinish,repeatsToFinish)
    }

    fun resume() {
        calculateTime(timeToFinishCurrentState)
    }

    fun reset(){
        timer.cancel()
        currentState = ExerciseState.INACTIVE
        repeatsToFinish = config.numberOfRepeats
        setsToFinish = config.numberOfSets
        viewModel.updateData(
            config.prepareTime,
            currentState,
            setsToFinish,
            repeatsToFinish)


    }
}