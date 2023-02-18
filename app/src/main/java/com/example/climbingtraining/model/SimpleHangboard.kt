package com.example.climbingtraining.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "savedSimpleConfigs")
data class SimpleHangboard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prepareTime: Long = 5000L,
    val hangTime: Long,
    val pauseTime: Long,
    val numberOfRepeats: Int,
    val restTime: Long,
    val numberOfSets: Int,
    val name: String
)
