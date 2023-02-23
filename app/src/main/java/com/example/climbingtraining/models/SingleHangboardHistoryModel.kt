package com.example.climbingtraining.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "singleTrainingHistory")
data class SingleHangboardHistoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date,
    val hangboardType : SingleHangboard = SingleHangboard(),
    val notes: String = "",
    val gripType: GripType = GripType.EDGE,
    val crimpType: CrimpType = CrimpType.OPEN_HAND,
    val edgeSize : Float = 0.0f,
    val slopeAngle : Float = 0.0f,
    val additionalWeight : Float = 0.0f,
)
