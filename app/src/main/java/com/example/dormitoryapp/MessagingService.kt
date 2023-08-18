package com.example.dormitoryapp


import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService : FirebaseMessagingService() {
    private val TAG = "FCM-SERVICE"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//        val data: Map<String, String> = message.data
//        val title = data["title"].toString()
//        val body = data["body"].toString()
//        val intent = Intent(this, MainActivity::class.java).apply {
//            putExtra("title",title)
//            putExtra("body", body)
//        }
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)

        val intent = Intent(INTENT_FILTER)
        remoteMessage.data.forEach { entity ->
            intent.putExtra(entity.key, entity.value)
        }

        sendBroadcast(intent)

        //message.notification?.body?.let { sendNotification(it) }


    }

    companion object {
        const val INTENT_FILTER = "PUSH_FILTER"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Token: $token")
    }

}