package com.example.myapplication

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

            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(keyword)}"))
            if (mapIntent.resolveActivity(packageManager) == null) {
                Toast.makeText(this, "当前设备没有可用地图应用", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(mapIntent)
            }
        }
    }
}
