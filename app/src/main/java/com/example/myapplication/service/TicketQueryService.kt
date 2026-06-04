package com.example.myapplication.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.myapplication.data.HometownData

class TicketQueryService : Service() {
    private val binder = TicketBinder()

    inner class TicketBinder : Binder() {
        fun getService(): TicketQueryService = this@TicketQueryService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind: 票价查询服务已绑定")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: 票价查询服务已解绑")
        return super.onUnbind(intent)
    }

    fun getTicketPrice(sceneryName: String): String {
        return HometownData.sceneries.firstOrNull { it.name == sceneryName }?.price ?: "未知"
    }

    companion object {
        private const val TAG = "TicketQueryService"
    }
}
