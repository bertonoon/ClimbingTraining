package com.example.climbingtraining.db

import android.app.Application

class HangboardApp : Application() {
    val db by lazy {
        HangboardDatabase.getInstance(this)
    }
}