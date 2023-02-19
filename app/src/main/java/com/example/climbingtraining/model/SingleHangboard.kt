package com.example.climbingtraining.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "savedSingleConfigs")
data class SingleHangboard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prepareTime: Long = 5000L,
    val hangTime: Long = 0,
    val pauseTime: Long = 0,
    val numberOfRepeats: Int =0,
    val restTime: Long =0,
    val numberOfSets: Int =0,
    val name: String = "Unnamed"
)
