package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.example.myapplication.data.HometownData

class AboutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val sceneryNames = HometownData.sceneries.joinToString("\n") { "· ${it.name}" }
        findViewById<TextView>(R.id.profileTextView).text = """
            姓名：请填写你的姓名
            班级：请填写你的班级
            学号：请填写你的学号
            教育经历：请填写你的教育经历
        """.trimIndent()
        findViewById<TextView>(R.id.aboutSceneryTextView).text = sceneryNames
    }
}
