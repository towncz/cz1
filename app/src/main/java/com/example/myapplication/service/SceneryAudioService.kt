package com.example.myapplication.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class SceneryAudioService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var playIndex = 0
    private var sceneryName = ""

    private val playRunnable = object : Runnable {
        override fun run() {
            if (playIndex >= audioTexts.size) {
                Log.d(TAG, "讲解结束：$sceneryName")
                stopSelf()
                return
            }

            Log.d(TAG, "正在讲解 $sceneryName：${audioTexts[playIndex]}")
            playIndex++
            handler.postDelayed(this, 3000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sceneryName = intent?.getStringExtra(EXTRA_SCENERY_NAME).orEmpty().ifBlank { "当前景点" }
        handler.removeCallbacks(playRunnable)
        playIndex = 0
        Log.d(TAG, "服务启动：$sceneryName")
        handler.post(playRunnable)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(playRunnable)
        Log.d(TAG, "服务销毁，清理讲解任务")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "SceneryAudioService"
        const val EXTRA_SCENERY_NAME = "extra_scenery_name"

        private val audioTexts = listOf(
            "这里是家乡代表性景点之一。",
            "游客可以了解它的历史文化与自然风貌。",
            "欢迎继续浏览其他家乡景点。"
        )
    }
}
