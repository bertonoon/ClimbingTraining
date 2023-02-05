package com.example.climbingtraining.model

data class SimpleHangboard(
    val prepareTime: Long,
    val hangTime: Long,
    val pauseTime: Long,
    val numberOfRounds: Int,
    val restTime: Long,
    val numberOfSets: Int
)
