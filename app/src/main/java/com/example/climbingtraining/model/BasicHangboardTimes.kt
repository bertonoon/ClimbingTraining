package com.example.climbingtraining.model

object BasicHangboardTimes {
    private val basicHangboardTimes = SimpleHangboard(
        prepareTime = 1000L,
        hangTime = 10000L,
        pauseTime = 15000L,
        restTime = 10000L,
        numberOfRounds = 2,
        numberOfSets = 2
    )
    fun getBasicTimes() = basicHangboardTimes
}