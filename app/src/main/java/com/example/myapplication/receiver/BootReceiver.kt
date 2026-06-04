package com.example.myapplication.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.data.HometownData
import com.example.myapplication.model.Scenery
import com.example.myapplication.ui.MainActivity
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            Log.w(TAG, "Ignore unsupported action: ${intent.action}")
            return
        }

        Log.d(TAG, "Device boot completed, preparing hometown recommendation.")
        createNotificationChannel(context)

        if (!canPostNotification(context)) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted; boot recommendation notification skipped.")
            return
        }

        val recommendedScenery = getTodayRecommendedScenery()
        val message = "今日推荐景点：${recommendedScenery.name}，欢迎游览！"
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_OPEN_APP,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("我的家乡景点导览")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BOOT_RECOMMENDATION, notification)
        Log.d(TAG, "Boot recommendation notification posted: $message")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "开机后推送今日推荐家乡景点"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        Log.d(TAG, "Notification channel ensured: $CHANNEL_ID")
    }

    private fun canPostNotification(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    private fun getTodayRecommendedScenery(): Scenery {
        val sceneries = HometownData.sceneries
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return sceneries[dayOfYear % sceneries.size]
    }

    companion object {
        private const val TAG = "BootReceiver"
        private const val CHANNEL_ID = "hometown_boot_recommendation"
        private const val CHANNEL_NAME = "开机推荐景点"
        private const val REQUEST_OPEN_APP = 1001
        private const val NOTIFICATION_ID_BOOT_RECOMMENDATION = 2001
    }
}
