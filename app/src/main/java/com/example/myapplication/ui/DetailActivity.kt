package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.SceneryImages
import com.example.myapplication.service.SceneryAudioService

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val intro = intent.getStringExtra(EXTRA_INTRO).orEmpty()
        val type = intent.getStringExtra(EXTRA_TYPE).orEmpty()
        val price = intent.getStringExtra(EXTRA_PRICE).orEmpty()
        val isOpen = intent.getBooleanExtra(EXTRA_IS_OPEN, false)

        setContent {
            HometownTheme {
                DetailScreen(
                    name = name,
                    intro = intro,
                    type = type,
                    price = price,
                    isOpen = isOpen,
                    onBackClick = { finish() },
                    onStartAudioClick = {
                        val serviceIntent = Intent(this, SceneryAudioService::class.java).apply {
                            putExtra(SceneryAudioService.EXTRA_SCENERY_NAME, name)
                        }
                        startService(serviceIntent)
                        Toast.makeText(this, "开始讲解：$name", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "开始讲解：$name")
                    },
                    onStopAudioClick = {
                        stopService(Intent(this, SceneryAudioService::class.java))
                        Toast.makeText(this, "停止讲解", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "停止讲解：$name")
                    },
                    onUpdateClick = {
                        sendBroadcast(Intent(MainActivity.ACTION_SCENERY_UPDATED).setPackage(packageName))
                        Toast.makeText(this, "已发送景点更新广播", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "发送景点更新广播：$name")
                    }
                )
            }
        }
    }

    companion object {
        private const val TAG = "DetailActivity"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_INTRO = "extra_intro"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_PRICE = "extra_price"
        const val EXTRA_IS_OPEN = "extra_is_open"
    }
}

@Composable
private fun DetailScreen(
    name: String,
    intro: String,
    type: String,
    price: String,
    isOpen: Boolean,
    onBackClick: () -> Unit,
    onStartAudioClick: () -> Unit,
    onStopAudioClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(SceneryImages.imageForName(name)),
            contentDescription = "${name}图片",
            modifier = Modifier.fillMaxWidth().height(230.dp),
            contentScale = ContentScale.Crop
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(intro)
                Text("类型：$type")
                Text("票价：$price")
                Text("开放状态：${if (isOpen) "开放" else "暂不开放"}")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onStartAudioClick, modifier = Modifier.weight(1f)) { Text("开始讲解") }
            OutlinedButton(onClick = onStopAudioClick, modifier = Modifier.weight(1f)) { Text("停止讲解") }
        }
        OutlinedButton(onClick = onUpdateClick, modifier = Modifier.fillMaxWidth()) { Text("景点已更新") }
        Spacer(Modifier.weight(1f))
        Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) { Text("返回列表") }
    }
}
