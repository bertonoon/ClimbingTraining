package com.example.climbingtraining.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.content.res.Resources.getSystem
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.example.climbingtraining.R
import com.example.climbingtraining.model.BasicHangboardTimes
import com.example.climbingtraining.model.ExerciseState
import com.example.climbingtraining.model.SingleHangboard
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class Exercise(
    private val viewModel: HangboardViewModel,
    private val context : Context
) {

    private var timeToFinishCurrentState : Long
    private lateinit var timer :CountDownTimer
    private var currentState : ExerciseState
    private var mediaPlayer: MediaPlayer? = null

    private var config : SingleHangboard =
        BasicHangboardTimes.getBasicTimes()
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

    fun setHangboard(newConfig : SingleHangboard){
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
                playSound()
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

    private fun playSound(){
        try{
            val soundURI = Uri.parse(
                "android.resource://com.example.climbingtraining/" + R.raw.start)
            mediaPlayer = MediaPlayer.create(context,soundURI)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
        } catch (e: Exception){
            e.printStackTrace()
        }
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