package com.example.softwareengineering

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("NOTIFICATION_MESSAGE")
        // Show the notification
        if (message != null) {
            NotificationUtils().showNotification(context, message)
        }
    }
}