package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class DetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val intro = intent.getStringExtra(EXTRA_INTRO).orEmpty()
        val type = intent.getStringExtra(EXTRA_TYPE).orEmpty()
        val price = intent.getStringExtra(EXTRA_PRICE).orEmpty()
        val isOpen = intent.getBooleanExtra(EXTRA_IS_OPEN, false)

        findViewById<TextView>(R.id.detailNameTextView).text = name
        findViewById<TextView>(R.id.detailIntroTextView).text = intro
        findViewById<TextView>(R.id.detailInfoTextView).text =
            "类型：$type\n票价：$price\n开放状态：${if (isOpen) "开放" else "暂不开放"}"

        findViewById<Button>(R.id.backToListButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.startAudioButton).setOnClickListener {
            val serviceIntent = Intent(this, SceneryAudioService::class.java).apply {
                putExtra(SceneryAudioService.EXTRA_SCENERY_NAME, name)
            }
            startService(serviceIntent)
            Toast.makeText(this, "开始讲解：$name", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "开始讲解：$name")
        }

        findViewById<Button>(R.id.stopAudioButton).setOnClickListener {
            stopService(Intent(this, SceneryAudioService::class.java))
            Toast.makeText(this, "停止讲解", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "停止讲解：$name")
        }

        findViewById<Button>(R.id.updateButton).setOnClickListener {
            sendBroadcast(Intent(MainActivity.ACTION_SCENERY_UPDATED).setPackage(packageName))
            Toast.makeText(this, "已发送景点更新广播", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "发送景点更新广播：$name")
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
