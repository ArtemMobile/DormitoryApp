package com.example.dormitoryapp

import android.content.Intent
import android.util.Log
import com.example.dormitoryapp.view.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    private val TAG = "FCM-SERVICE"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data: Map<String, String> = message.data
        val title = data["title"].toString()
        val body = data["body"].toString()
        startActivity(Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("title",title)
            putExtra("body", body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New Token: $token")
    }
}