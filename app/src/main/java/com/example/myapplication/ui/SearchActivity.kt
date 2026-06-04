package com.example.myapplication.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HometownTheme {
                SearchScreen(
                    onSearch = { keyword ->
                        if (keyword.isBlank()) {
                            Toast.makeText(this, "请输入景点关键词", Toast.LENGTH_SHORT).show()
                        } else {
                            openMapSearch(keyword.trim())
                        }
                    },
                    onBack = { finish() }
                )
            }
        }
    }

    private fun openMapSearch(keyword: String) {
        val encodedKeyword = Uri.encode(keyword)
        val mapIntents = listOf(
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encodedKeyword")),
            Intent(Intent.ACTION_VIEW, Uri.parse("androidamap://poi?sourceApplication=${packageName}&keywords=$encodedKeyword&dev=0")),
            Intent(Intent.ACTION_VIEW, Uri.parse("baidumap://map/place/search?query=$encodedKeyword&region=${Uri.encode("无锡")}"))
        )

        for (intent in mapIntents) {
            try {
                startActivity(intent)
                return
            } catch (_: ActivityNotFoundException) {
                // Continue to the next map protocol.
            }
        }

        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedKeyword"))
            )
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "当前设备没有可用地图或浏览器应用", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun SearchScreen(onSearch: (String) -> Unit, onBack: () -> Unit) {
    var keyword by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("景点搜索", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("输入景点关键词") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { onSearch(keyword) }, modifier = Modifier.fillMaxWidth()) {
            Text("打开系统地图搜索")
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("返回")
        }
    }
}
