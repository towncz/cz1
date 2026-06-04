package com.example.myapplication.ui

import android.Manifest
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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.data.HometownData
import com.example.myapplication.data.SceneryImages
import com.example.myapplication.model.Scenery
import com.example.myapplication.receiver.NetworkReceiver
import com.example.myapplication.receiver.SceneryUpdateReceiver
import com.example.myapplication.service.TicketQueryService

class MainActivity : ComponentActivity() {
    private var ticketQueryService: TicketQueryService? = null
    private var isTicketServiceBound = false
    private val networkReceiver = NetworkReceiver()
    private var isNetworkReceiverRegistered = false
    private val sceneryUpdateReceiver = SceneryUpdateReceiver {
        refreshSceneryList()
    }
    private var isSceneryUpdateReceiverRegistered = false
    private var refreshVersion by mutableIntStateOf(0)

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
        requestPostNotificationPermissionIfNeeded()
        registerSceneryUpdateReceiver()

        setContent {
            HometownTheme {
                MainScreen(
                    refreshVersion = refreshVersion,
                    onSearchClick = { startActivity(Intent(this, SearchActivity::class.java)) },
                    onAboutClick = { startActivity(Intent(this, AboutActivity::class.java)) },
                    onProviderClick = { startActivity(Intent(this, ProviderTestActivity::class.java)) },
                    onServiceDemoClick = { openDetail(HometownData.sceneries.first()) },
                    onBroadcastDemoClick = {
                        sendBroadcast(Intent(ACTION_SCENERY_UPDATED).setPackage(packageName))
                        Toast.makeText(this, "已发送景点更新广播", Toast.LENGTH_SHORT).show()
                    },
                    onSceneryClick = { scenery ->
                        val price = ticketQueryService?.getTicketPrice(scenery.name) ?: scenery.price
                        Toast.makeText(this, "${scenery.name} 票价：$price", Toast.LENGTH_SHORT).show()
                        openDetail(scenery, price)
                    }
                )
            }
        }

        Log.d(TAG, "MainActivity onCreate: Compose 景点列表页面创建完成")
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, TicketQueryService::class.java), ticketServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        registerNetworkReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkReceiver()
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

    override fun onDestroy() {
        unregisterSceneryUpdateReceiver()
        super.onDestroy()
    }

    private fun openDetail(scenery: Scenery, price: String = scenery.price) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_NAME, scenery.name)
            putExtra(DetailActivity.EXTRA_INTRO, scenery.intro)
            putExtra(DetailActivity.EXTRA_TYPE, scenery.type)
            putExtra(DetailActivity.EXTRA_PRICE, price)
            putExtra(DetailActivity.EXTRA_IS_OPEN, scenery.isOpen)
        }
        startActivity(intent)
    }

    private fun requestPostNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_POST_NOTIFICATIONS)
        }
    }

    private fun registerNetworkReceiver() {
        if (isNetworkReceiverRegistered) return
        ContextCompat.registerReceiver(
            this,
            networkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
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
        ContextCompat.registerReceiver(
            this,
            sceneryUpdateReceiver,
            IntentFilter(ACTION_SCENERY_UPDATED),
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
        refreshVersion++
        Log.d(TAG, "景点列表已刷新，当前景点数量：${HometownData.sceneries.size}")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_POST_NOTIFICATIONS = 100
        const val ACTION_SCENERY_UPDATED = "com.example.myapplication.action.SCENERY_UPDATED"
    }
}

@Composable
fun HometownTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

@Composable
private fun MainScreen(
    refreshVersion: Int,
    onSearchClick: () -> Unit,
    onAboutClick: () -> Unit,
    onProviderClick: () -> Unit,
    onServiceDemoClick: () -> Unit,
    onBroadcastDemoClick: () -> Unit,
    onSceneryClick: (Scenery) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("我的家乡景点导览", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("无锡 6 个真实景点，完整演示 Activity、Service、BroadcastReceiver、ContentProvider")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onSearchClick, modifier = Modifier.weight(1f)) { Text("景点搜索") }
            Button(onClick = onAboutClick, modifier = Modifier.weight(1f)) { Text("个人主页") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            OutlinedButton(onClick = onProviderClick, modifier = Modifier.weight(1f)) { Text("Provider 测试") }
            OutlinedButton(onClick = onServiceDemoClick, modifier = Modifier.weight(1f)) { Text("Service 演示") }
        }
        OutlinedButton(onClick = onBroadcastDemoClick, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("BroadcastReceiver 演示")
        }
        Spacer(Modifier.height(12.dp))
        Text("景点列表 刷新版本：$refreshVersion", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(HometownData.sceneries, key = { it.name }) { scenery ->
                SceneryCard(scenery, onClick = { onSceneryClick(scenery) })
            }
        }
    }
}

@Composable
private fun SceneryCard(scenery: Scenery, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(
                painter = painterResource(SceneryImages.imageForName(scenery.name)),
                contentDescription = "${scenery.name}图片",
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Text(scenery.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("类型：${scenery.type}    票价：${scenery.price}    状态：${if (scenery.isOpen) "开放" else "暂不开放"}")
            Text(scenery.intro)
        }
    }
}
