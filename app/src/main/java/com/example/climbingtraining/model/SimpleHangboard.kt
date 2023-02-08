package com.example.climbingtraining.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleHangboard(
    val prepareTime: Long,
    val hangTime: Long,
    val pauseTime: Long,
    val numberOfRepeats: Int,
    val restTime: Long,
    val numberOfSets: Int
) : Parcelable
