package com.example.climbingtraining.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lastHangboard")
data class LastHangboard(
    @PrimaryKey
    val id: Int = 1,
    val prepareTime: Long = 5000L,
    val hangTime: Long = 1000L,
    val pauseTime: Long = 1000L,
    val numberOfRepeats: Int = 1,
    val restTime: Long = 1000,
    val numberOfSets: Int = 1,
    val name: String = "Unnamed"
)