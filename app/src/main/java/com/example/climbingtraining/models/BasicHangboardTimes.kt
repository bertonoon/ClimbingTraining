package com.example.climbingtraining.models

object BasicHangboardTimes {
    private val basicHangboardTimes = SingleHangboard(
        prepareTime = 1000L,
        hangTime = 1000L,
        pauseTime = 1000L,
        restTime = 1000L,
        numberOfRepeats = 1,
        numberOfSets = 1,
        name = "Default"
    )
    fun getBasicTimes() = basicHangboardTimes
}