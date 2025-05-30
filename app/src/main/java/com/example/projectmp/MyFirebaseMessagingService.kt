package com.example.projectmp

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Mengecek apakah pesan berisi data
        if (remoteMessage.data.isNotEmpty()) {
            val message = remoteMessage.data["message"]
            sendNotification(message)
        }

        // Mengecek apakah pesan berisi notifikasi
        remoteMessage.notification?.let {
            val notificationTitle = it.title
            val notificationBody = it.body
            sendNotification(notificationBody)
        }
    }

    private fun sendNotification(messageBody: String?) {
        // Membuat notifikasi
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.dc1)
            .setContentTitle("Pesanan Baru")
            .setContentText(messageBody ?: "Ada pesanan baru masuk")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Menampilkan notifikasi
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // Mengirimkan token baru ke server jika perlu
        super.onNewToken(token)
    }
}
