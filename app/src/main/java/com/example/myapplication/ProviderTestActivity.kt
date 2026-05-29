package com.example.myapplication

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.data.HometownData
import com.example.myapplication.data.SceneryContract
import com.example.myapplication.data.SceneryImages

class ProviderTestActivity : Activity() {
    private lateinit var resultTextView: TextView
    private lateinit var imageGallery: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_test)

        resultTextView = findViewById(R.id.providerResultTextView)
        imageGallery = findViewById(R.id.providerImageGallery)
        showDefaultImages()
        findViewById<Button>(R.id.runProviderTestButton).setOnClickListener {
            runProviderTest()
        }
    }

    private fun runProviderTest() {
        val builder = StringBuilder()

        logAllSceneries(builder, "初始查询全部景点", renderImages = false)

        val insertValues = HometownData.createSceneryValues(
            name = "清名桥古运河",
            intro = "无锡古运河精华河段，水巷夜景很有特色。",
            type = "休闲",
            price = "免费",
            isOpen = true
        )
        val insertedUri = contentResolver.insert(SceneryContract.CONTENT_URI, insertValues)
        Log.d(TAG, "插入自编景点：$insertedUri")
        builder.appendLine("插入自编景点：$insertedUri")

        val deletedCount = contentResolver.delete(
            ContentUris.withAppendedId(SceneryContract.CONTENT_URI, 1),
            null,
            null
        )
        Log.d(TAG, "删除 id=1 景点，影响行数：$deletedCount")
        builder.appendLine("删除 id=1 景点，影响行数：$deletedCount")

        val updateValues = ContentValues().apply {
            put(SceneryContract.SceneryColumns.IS_OPEN, 0)
        }
        val updatedCount = contentResolver.update(
            ContentUris.withAppendedId(SceneryContract.CONTENT_URI, 2),
            updateValues,
            null,
            null
        )
        Log.d(TAG, "更新 id=2 is_open=0，影响行数：$updatedCount")
        builder.appendLine("更新 id=2 is_open=0，影响行数：$updatedCount")

        logAllSceneries(builder, "操作后再次查询全部景点", renderImages = true)
        resultTextView.text = builder.toString()
        Toast.makeText(this, "ContentProvider 测试已执行，请查看 Logcat", Toast.LENGTH_SHORT).show()
    }

    private fun logAllSceneries(builder: StringBuilder, title: String, renderImages: Boolean) {
        Log.d(TAG, title)
        builder.appendLine(title)
        if (renderImages) {
            imageGallery.removeAllViews()
        }

        contentResolver.query(
            SceneryContract.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.ID)
            val nameIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.NAME)
            val typeIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.TYPE)
            val priceIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.PRICE)
            val openIndex = cursor.getColumnIndexOrThrow(SceneryContract.SceneryColumns.IS_OPEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val line = "id=$id, " +
                    "name=$name, " +
                    "type=${cursor.getString(typeIndex)}, " +
                    "price=${cursor.getString(priceIndex)}, " +
                    "is_open=${cursor.getInt(openIndex)}"
                Log.d(TAG, line)
                builder.appendLine(line)
                if (renderImages) {
                    addProviderImage(name)
                }
            }
        }
    }

    private fun showDefaultImages() {
        imageGallery.removeAllViews()
        HometownData.sceneries.forEach { scenery ->
            addProviderImage(scenery.name)
        }
    }

    private fun addProviderImage(sceneryName: String) {
        val imageWidth = resources.getDimensionPixelSize(R.dimen.scenery_gallery_image_width)
        val imageHeight = resources.getDimensionPixelSize(R.dimen.scenery_gallery_image_height)
        val imageGap = resources.getDimensionPixelSize(R.dimen.scenery_gallery_image_gap)

        imageGallery.addView(ImageView(this).apply {
            setImageResource(SceneryImages.imageForName(sceneryName))
            contentDescription = "${sceneryName}图片"
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = LinearLayout.LayoutParams(imageWidth, imageHeight).apply {
                marginEnd = imageGap
            }
        })
    }

    companion object {
        private const val TAG = "ProviderTestActivity"
    }
}
