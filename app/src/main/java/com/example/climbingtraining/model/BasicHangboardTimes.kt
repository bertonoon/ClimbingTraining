package com.example.climbingtraining.model

object BasicHangboardTimes {
    private val basicHangboardTimes = SingleHangboard(
        prepareTime = 500L,
        hangTime = 500L,
        pauseTime = 500L,
        restTime = 500L,
        numberOfRepeats = 2,
        numberOfSets = 2,
        name = ""
    )
    fun getBasicTimes() = basicHangboardTimes
}