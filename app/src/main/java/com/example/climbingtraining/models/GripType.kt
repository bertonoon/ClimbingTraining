package com.example.climbingtraining.models

enum class GripType {
    UNDEFINED,
    EDGE,
    SLOPER,
    JUG,
    PINCH,
    ONE_FINGER,
    TWO_FINGER,
    THREE_FINGER,
    OTHER;

    fun toDisplayString(): String {
        return this.toString().lowercase().replaceFirstChar(Char::titlecase).replace("_"," ")
    }
}
