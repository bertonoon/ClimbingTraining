package com.example.climbingtraining.models

enum class ExerciseState(val state: Int) {
    INACTIVE(0),
    PREPARE(1),
    HANG(2),
    PAUSE(3),
    REST(4);

    override fun toString(): String {
        return super.toString().lowercase().replaceFirstChar(Char::titlecase)
    }
}
