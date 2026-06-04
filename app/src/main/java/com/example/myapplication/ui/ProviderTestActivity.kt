package com.example.myapplication.ui

import android.content.ContentUris
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.HometownData
import com.example.myapplication.data.SceneryContract
import com.example.myapplication.data.SceneryImages

class ProviderTestActivity : ComponentActivity() {
    private var resultText by mutableStateOf("点击按钮后，将通过 ContentResolver 执行查询、插入、删除、更新并写入 Logcat。")
    private var imageNames by mutableStateOf(HometownData.sceneries.map { it.name })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HometownTheme {
                ProviderTestScreen(
                    resultText = resultText,
                    imageNames = imageNames,
                    onRunTest = { runProviderTest() },
                    onBack = { finish() }
                )
            }
        }
    }

    private fun runProviderTest() {
        val builder = StringBuilder()
        logAllSceneries(builder, "初始查询全部景点", renderImages = false)

        val insertedUri = contentResolver.insert(
            SceneryContract.CONTENT_URI,
            HometownData.createSceneryValues(
                name = "清名桥古运河",
                intro = "无锡古运河精华河段，水巷夜景很有特色。",
                type = "休闲",
                price = "免费",
                isOpen = true
            )
        )
        Log.d(TAG, "插入自编景点：$insertedUri")
        builder.appendLine("插入自编景点：$insertedUri")

        val deletedCount = contentResolver.delete(
            ContentUris.withAppendedId(SceneryContract.CONTENT_URI, 1),
            null,
            null
        )
        Log.d(TAG, "删除 id=1 景点，影响行数：$deletedCount")
        builder.appendLine("删除 id=1 景点，影响行数：$deletedCount")

        val updatedCount = contentResolver.update(
            ContentUris.withAppendedId(SceneryContract.CONTENT_URI, 2),
            ContentValues().apply { put(SceneryContract.SceneryColumns.IS_OPEN, 0) },
            null,
            null
        )
        Log.d(TAG, "更新 id=2 is_open=0，影响行数：$updatedCount")
        builder.appendLine("更新 id=2 is_open=0，影响行数：$updatedCount")

        logAllSceneries(builder, "操作后再次查询全部景点", renderImages = true)
        resultText = builder.toString()
        Toast.makeText(this, "ContentProvider 测试已执行，请查看 Logcat", Toast.LENGTH_SHORT).show()
    }

    private fun logAllSceneries(builder: StringBuilder, title: String, renderImages: Boolean) {
        Log.d(TAG, title)
        builder.appendLine(title)
        val names = mutableListOf<String>()

        contentResolver.query(SceneryContract.CONTENT_URI, null, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.ID)
            val nameIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.NAME)
            val typeIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.TYPE)
            val priceIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.PRICE)
            val openIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.IS_OPEN)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val line = "id=${cursor.getLong(idIndex)}, name=$name, type=${cursor.getString(typeIndex)}, price=${cursor.getString(priceIndex)}, is_open=${cursor.getInt(openIndex)}"
                Log.d(TAG, line)
                builder.appendLine(line)
                names.add(name)
            }
        }

        if (renderImages) {
            imageNames = names
        }
    }

    companion object {
        private const val TAG = "ProviderTestActivity"
    }
}

@Composable
private fun ProviderTestScreen(
    resultText: String,
    imageNames: List<String>,
    onRunTest: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("ContentProvider 测试", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Button(onClick = onRunTest, modifier = Modifier.fillMaxWidth()) {
            Text("执行增删改查测试")
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(resultText, modifier = Modifier.padding(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            imageNames.forEach { name ->
                Image(
                    painter = painterResource(SceneryImages.imageForName(name)),
                    contentDescription = "${name}图片",
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
