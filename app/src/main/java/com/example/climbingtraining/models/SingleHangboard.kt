package com.example.climbingtraining.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "savedSingleConfigs")
data class SingleHangboard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prepareTime: Long = 5000L,
    val hangTime: Long = 1000L,
    val pauseTime: Long = 1000L,
    val numberOfRepeats: Int = 1,
    val restTime: Long = 1000,
    val numberOfSets: Int = 1,
    val name: String = "Unnamed"
)
