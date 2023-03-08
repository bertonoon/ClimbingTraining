package com.example.climbingtraining.models

object BasicHangboardTimes {
    private val basicHangboardTimes = SingleHangboard(
        prepareTime = 10000L,
        hangTime = 7000L,
        pauseTime = 90000L,
        restTime = 3000L,
        numberOfRepeats = 6,
        numberOfSets = 2,
        name = "Default"
    )

    fun getBasicTimes() = basicHangboardTimes
}