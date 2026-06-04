package com.example.myapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.HometownData
import com.example.myapplication.data.SceneryImages

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HometownTheme {
                AboutScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
private fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("个人主页", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Image(
            painter = painterResource(R.drawable.personalpic),
            contentDescription = "个人图片",
            modifier = Modifier.fillMaxWidth().height(260.dp),
            contentScale = ContentScale.Crop
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("姓名：陈振")
                Text("班级：物联网2304")
                Text("学号：1034230415")
                Text("教育经历：江南大学 本科")
            }
        }
        Text("家乡景点", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        HometownData.sceneries.forEach { scenery ->
            Text("• ${scenery.name}")
        }
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HometownData.sceneries.forEach { scenery ->
                Image(
                    painter = painterResource(SceneryImages.imageForName(scenery.name)),
                    contentDescription = "${scenery.name}图片",
                    modifier = Modifier.width(180.dp).height(120.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("返回")
        }
    }
}
