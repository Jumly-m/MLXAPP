package com.nullsolutions.mlxapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nullsolutions.mlxapp.utils.sendNotification

class FCM : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            val title: String = remoteMessage.notification!!.title.toString()
            val body: String = remoteMessage.notification!!.body.toString()
            sendNotification(this, title, body)
        }
    }
}