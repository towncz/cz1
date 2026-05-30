package com.example.myapplication

import android.content.ActivityNotFoundException
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SearchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val keywordEditText = findViewById<EditText>(R.id.keywordEditText)
        findViewById<Button>(R.id.mapSearchButton).setOnClickListener {
            val keyword = keywordEditText.text.toString().trim()
            if (keyword.isEmpty()) {
                Toast.makeText(this, "请输入景点关键词", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            openMapSearch(keyword)
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
                // Try the next common map protocol.
            }
        }

        val browserFallback = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedKeyword")
        )
        try {
            startActivity(browserFallback)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "当前设备没有可用地图或浏览器应用", Toast.LENGTH_SHORT).show()
        }
    }
}
