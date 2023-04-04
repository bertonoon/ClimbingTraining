package com.example.climbingtraining.utils

class Constants {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1"
        const val NOTIFICATION_CHANNEL_NAME = "Hangboard timer"
        const val REQUEST_PERMISSION_CODE = 0

        const val NOTIFICATION_ACTION_MESSAGE = "Notification action message"
        const val START_FROM_NOTIFICATION = "Start from notification"
        const val STOP_FROM_NOTIFICATION = "Stop from notification"
        const val START_REQUEST_CODE = 5
        const val STOP_REQUEST_CODE = 6
        const val CONTENT_INTENT_REQUEST_CODE = 7

        const val RECEIVER_START_TIMER = "Start timer from broadcast receiver"
        const val RECEIVER_STOP_TIMER = "Stop timer from broadcast receiver"
    }
}