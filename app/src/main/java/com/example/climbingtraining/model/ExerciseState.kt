package com.example.climbingtraining.model

enum class ExerciseState(val state: Int) {
    INACTIVE(0),
    PREPARE(1),
    HANG(2),
    PAUSE(3),
    REST(4)
}