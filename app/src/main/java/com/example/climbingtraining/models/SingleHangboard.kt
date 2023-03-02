package com.example.climbingtraining.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "savedSingleConfigs")
data class SingleHangboard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prepareTime: Long = 10000L,
    val hangTime: Long = 7000L,
    val pauseTime: Long = 90000L,
    val numberOfRepeats: Int = 6,
    val restTime: Long = 3000L,
    val numberOfSets: Int = 2,
    val name: String = "Unnamed"
)
