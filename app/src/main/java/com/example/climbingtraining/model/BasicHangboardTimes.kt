package com.example.climbingtraining.model

object BasicHangboardTimes {
    private val basicHangboardTimes = SimpleHangboard(
        prepareTime = 1000L,
        hangTime = 1000L,
        pauseTime = 1500L,
        restTime = 1000L,
        numberOfRounds = 2,
        numberOfSets = 2
    )
    fun getBasicTimes() = basicHangboardTimes
}