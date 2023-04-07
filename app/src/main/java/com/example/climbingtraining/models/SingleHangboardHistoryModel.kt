package com.example.climbingtraining.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "singleTrainingHistory")
data class SingleHangboardHistoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date = Date(),
    val hangboardType: SingleHangboard = SingleHangboard(),
    val notes: String = "",
    val gripType: GripType = GripType.UNDEFINED,
    val crimpType: CrimpType = CrimpType.UNDEFINED,
    val edgeSize: Int = 0,
    val slopeAngle: Int = 0,
    val additionalWeight: Float = 0.0f,
    val intensity : Int = 0
)

