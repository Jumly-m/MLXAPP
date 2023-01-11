package com.nullsolutions.mlxapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging

class MLXApp : Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
        FirebaseMessaging.getInstance().subscribeToTopic("broadcast")
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "NEWS",
                "NEWS",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "UPDATED NEWS WILL SEND BY THIS CHANEL"
            channel.enableLights(true)
            channel.enableVibration(true)
            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            channel.setSound(uri, att)
            val manager = getSystemService(
                NotificationManager::class.java
            )!!
            manager.createNotificationChannel(channel)
        }
    }
}