package com.example.climbingtraining.utils

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import com.example.climbingtraining.R
import com.example.climbingtraining.models.BasicHangboardTimes
import com.example.climbingtraining.models.ExerciseState
import com.example.climbingtraining.models.SingleHangboard
import com.example.climbingtraining.ui.viewModels.HangboardViewModel


class Exercise(
    private val viewModel: HangboardViewModel,
    private val context: Context
) {

    private var timeToFinishCurrentState: Long
    private lateinit var timer: CountDownTimer
    private var currentState: ExerciseState
    private var mediaPlayer: MediaPlayer? = null

    private var config: SingleHangboard =
        BasicHangboardTimes.getBasicTimes()
    private var setsToFinish: Int = config.numberOfSets
    private var repeatsToFinish: Int = config.numberOfRepeats


    init {
        currentState = ExerciseState.INACTIVE
        timeToFinishCurrentState = 0
        viewModel.updateData(
            timeToFinishCurrentState,
            currentState,
            setsToFinish,
            repeatsToFinish
        )
    }

    fun setHangboard(newConfig: SingleHangboard) {
        config = newConfig
        setsToFinish = config.numberOfSets
        repeatsToFinish = config.numberOfRepeats
    }

    fun getHangboard() = config

    private fun calculateTime(time: Long) {
        timer = object : CountDownTimer(time, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeToFinishCurrentState = millisUntilFinished
                viewModel.updateData(
                    timeToFinishCurrentState,
                    currentState,
                    setsToFinish,
                    repeatsToFinish
                )
                updateNotification(
                    "Time to finish: ${timeToFinishCurrentState / 1000} s",
                    currentState.toString()
                )
            }

            override fun onFinish() {
                timeToFinishCurrentState = 0
                playSound()
                when (currentState) {
                    ExerciseState.REST -> repeatsToFinish--
                    ExerciseState.PAUSE -> setsToFinish--
                    ExerciseState.HANG -> {
                        if (repeatsToFinish < 2 && setsToFinish < 2) {
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

    fun run() {
        nextState()
        startNotification()
    }

    private fun playSound() {
        try {
            val packageName = context.packageName
            val soundURI = Uri.parse(
                "android.resource://$packageName/" + R.raw.start
            )
            mediaPlayer = MediaPlayer.create(context, soundURI)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun nextState() {
        when (currentState) {
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
            repeatsToFinish
        )
    }

    fun getState() = currentState
    fun getTimeToFinish() = timeToFinishCurrentState


    fun stop() {
        timer.cancel()
        viewModel.updateData(timeToFinishCurrentState, currentState, setsToFinish, repeatsToFinish)
    }

    fun resume() {
        calculateTime(timeToFinishCurrentState)
    }

    fun reset() {
        timer.cancel()
        currentState = ExerciseState.INACTIVE
        repeatsToFinish = config.numberOfRepeats
        setsToFinish = config.numberOfSets
        viewModel.updateData(
            config.prepareTime,
            currentState,
            setsToFinish,
            repeatsToFinish
        )
        stopNotification()
    }


    private fun startNotification() {
        val serviceIntent = Intent(context, HangboardService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun updateNotification(text: String, title: String) {
        val serviceIntent = Intent(context, HangboardService::class.java)
        serviceIntent.putExtra("contentText", text)
        serviceIntent.putExtra("contentTitle", title)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopNotification() {
        val serviceIntent = Intent(context, HangboardService::class.java)
        context.stopService(serviceIntent)
    }


}