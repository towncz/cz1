package com.example.myapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.ui.MainActivity

class SceneryUpdateReceiver(
    private val onSceneryUpdated: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != MainActivity.ACTION_SCENERY_UPDATED) {
            Log.w(TAG, "Ignore unsupported action: ${intent.action}")
            return
        }

        Log.d(TAG, "收到景点更新广播，准备刷新列表")
        onSceneryUpdated()
    }

    companion object {
        private const val TAG = "SceneryUpdateReceiver"
    }
}
