package com.example.climbingtraining.utils

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.climbingtraining.R
import com.example.climbingtraining.ui.activities.HangboardActivity
import java.util.*


class HangboardService : Service() {

    private var notificationBuilder: NotificationCompat.Builder? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val contentText = intent?.getStringExtra("contentText");
        val contentTitle = intent?.getStringExtra("contentTitle");

        val notificationIntent = Intent(this, HangboardActivity::class.java)
        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE
            else
                0

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flag)

        notificationBuilder =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .apply {
                    setSmallIcon(R.drawable.ic_baseline_watch_later_24)
                    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    setContentIntent(pendingIntent)
                    setAutoCancel(false)
                    setOngoing(true)
                    setDefaults(0)
                    setVibrate(null)
                    setSound(null)
                    setContentText(contentText)
                    setContentTitle(contentTitle)
                    foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }

        startForeground(1, notificationBuilder!!.build())

        return START_NOT_STICKY
    }


}