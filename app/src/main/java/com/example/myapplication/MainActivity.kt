package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.HometownData

class MainActivity : Activity() {
    private var ticketQueryService: TicketQueryService? = null
    private var isTicketServiceBound = false
    private lateinit var sceneryAdapter: SceneryAdapter
    private val networkReceiver = NetworkReceiver()
    private var isNetworkReceiverRegistered = false
    private val sceneryUpdateReceiver = SceneryUpdateReceiver {
        refreshSceneryList()
    }
    private var isSceneryUpdateReceiverRegistered = false

    private val ticketServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TicketQueryService.TicketBinder
            ticketQueryService = binder.getService()
            isTicketServiceBound = true
            Log.d(TAG, "TicketQueryService 已绑定")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ticketQueryService = null
            isTicketServiceBound = false
            Log.d(TAG, "TicketQueryService 连接断开")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPostNotificationPermissionIfNeeded()
        registerSceneryUpdateReceiver()

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        findViewById<Button>(R.id.aboutButton).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        findViewById<Button>(R.id.providerTestButton).setOnClickListener {
            startActivity(Intent(this, ProviderTestActivity::class.java))
        }

        val sceneryRecyclerView = findViewById<RecyclerView>(R.id.sceneryRecyclerView)
        sceneryRecyclerView.layoutManager = LinearLayoutManager(this)
        sceneryAdapter = SceneryAdapter(HometownData.sceneries) { scenery ->
            val price = ticketQueryService?.getTicketPrice(scenery.name) ?: scenery.price
            Toast.makeText(this, "${scenery.name} 票价：$price", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_NAME, scenery.name)
                putExtra(DetailActivity.EXTRA_INTRO, scenery.intro)
                putExtra(DetailActivity.EXTRA_TYPE, scenery.type)
                putExtra(DetailActivity.EXTRA_PRICE, price)
                putExtra(DetailActivity.EXTRA_IS_OPEN, scenery.isOpen)
            }
            startActivity(intent)
        }
        sceneryRecyclerView.adapter = sceneryAdapter

        Log.d(TAG, "MainActivity onCreate: 景点列表页面创建完成")
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TicketQueryService::class.java)
        bindService(intent, ticketServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        registerNetworkReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSceneryUpdateReceiver()
    }

    override fun onStop() {
        super.onStop()
        if (isTicketServiceBound) {
            unbindService(ticketServiceConnection)
            isTicketServiceBound = false
            ticketQueryService = null
            Log.d(TAG, "TicketQueryService 已解绑")
        }
    }

    private fun requestPostNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_POST_NOTIFICATIONS
            )
        }
    }

    private fun registerNetworkReceiver() {
        if (isNetworkReceiverRegistered) return

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        ContextCompat.registerReceiver(
            this,
            networkReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        isNetworkReceiverRegistered = true
        Log.d(TAG, "NetworkReceiver registered.")
    }

    private fun unregisterNetworkReceiver() {
        if (!isNetworkReceiverRegistered) return

        unregisterReceiver(networkReceiver)
        isNetworkReceiverRegistered = false
        Log.d(TAG, "NetworkReceiver unregistered.")
    }

    private fun registerSceneryUpdateReceiver() {
        if (isSceneryUpdateReceiverRegistered) return

        val intentFilter = IntentFilter(ACTION_SCENERY_UPDATED)
        ContextCompat.registerReceiver(
            this,
            sceneryUpdateReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        isSceneryUpdateReceiverRegistered = true
        Log.d(TAG, "SceneryUpdateReceiver registered.")
    }

    private fun unregisterSceneryUpdateReceiver() {
        if (!isSceneryUpdateReceiverRegistered) return

        unregisterReceiver(sceneryUpdateReceiver)
        isSceneryUpdateReceiverRegistered = false
        Log.d(TAG, "SceneryUpdateReceiver unregistered.")
    }

    private fun refreshSceneryList() {
        sceneryAdapter.notifyDataSetChanged()
        Log.d(TAG, "景点列表已刷新，当前景点数量：${HometownData.sceneries.size}")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_POST_NOTIFICATIONS = 100
        const val ACTION_SCENERY_UPDATED = "com.example.myapplication.action.SCENERY_UPDATED"
    }
}
