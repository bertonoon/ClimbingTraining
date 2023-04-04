package com.example.climbingtraining.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HangboardReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.getStringExtra(Constants.NOTIFICATION_ACTION_MESSAGE)) {
            Constants.START_FROM_NOTIFICATION -> {
                context?.sendBroadcast(Intent(Constants.RECEIVER_START_TIMER))
            }
            Constants.STOP_FROM_NOTIFICATION -> {
                context?.sendBroadcast(Intent(Constants.RECEIVER_STOP_TIMER))
            }
        }
    }
}
