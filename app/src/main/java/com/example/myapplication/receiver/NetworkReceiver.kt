package com.example.myapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast

class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ConnectivityManager.CONNECTIVITY_ACTION) {
            Log.w(TAG, "Ignore unsupported action: ${intent.action}")
            return
        }

        if (isNetworkConnected(context)) {
            Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Network connected.")
        } else {
            Toast.makeText(context, "当前无网络，景点地图功能不可用", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Network disconnected.")
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    companion object {
        private const val TAG = "NetworkReceiver"
    }
}
