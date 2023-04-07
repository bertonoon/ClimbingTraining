package com.example.climbingtraining.models

enum class CrimpType {
    UNDEFINED,
    OPEN_HAND,
    OPEN_CRIMP,
    CLOSED_CRIMP;

    fun shortcutString() : String{
        return when(this){
            UNDEFINED -> "XX"
            OPEN_HAND -> "OH"
            OPEN_CRIMP -> "OC"
            CLOSED_CRIMP -> "CC"
        }
    }
    fun toDisplayString() : String{
        return when(this){
            UNDEFINED -> "Undefined"
            OPEN_HAND -> "Open hand"
            OPEN_CRIMP -> "Open crimp"
            CLOSED_CRIMP -> "Closed crimp"
        }
    }

}