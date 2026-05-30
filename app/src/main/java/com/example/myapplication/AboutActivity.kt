package com.example.myapplication

import android.app.Activity
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.myapplication.data.HometownData
import com.example.myapplication.data.SceneryImages

class AboutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val sceneryNames = HometownData.sceneries.joinToString("\n") { "· ${it.name}" }
        findViewById<TextView>(R.id.profileTextView).text = """
            姓名：陈振
            班级：物联网2304
            学号：1034230415
            教育经历：江南大学 本科
        """.trimIndent()
        findViewById<TextView>(R.id.aboutSceneryTextView).text = sceneryNames
        alignProfilePhotoTop()
        fillImageGallery()
    }

    private fun alignProfilePhotoTop() {
        val imageView = findViewById<ImageView>(R.id.profileImageView)
        imageView.post {
            val drawable = imageView.drawable ?: return@post
            val viewWidth = imageView.width - imageView.paddingLeft - imageView.paddingRight
            val viewHeight = imageView.height - imageView.paddingTop - imageView.paddingBottom
            val scale = maxOf(
                viewWidth.toFloat() / drawable.intrinsicWidth,
                viewHeight.toFloat() / drawable.intrinsicHeight
            )
            val dx = (viewWidth - drawable.intrinsicWidth * scale) / 2f + imageView.paddingLeft
            val dy = imageView.paddingTop.toFloat()
            imageView.imageMatrix = Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx, dy)
            }
        }
    }

    private fun fillImageGallery() {
        val gallery = findViewById<LinearLayout>(R.id.aboutImageGallery)
        val imageWidth = resources.getDimensionPixelSize(R.dimen.scenery_gallery_image_width)
        val imageHeight = resources.getDimensionPixelSize(R.dimen.scenery_gallery_image_height)
        val imageGap = resources.getDimensionPixelSize(R.dimen.scenery_gallery_image_gap)

        gallery.removeAllViews()
        HometownData.sceneries.forEach { scenery ->
            gallery.addView(ImageView(this).apply {
                setImageResource(SceneryImages.imageForName(scenery.name))
                contentDescription = "${scenery.name}图片"
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = LinearLayout.LayoutParams(imageWidth, imageHeight).apply {
                    marginEnd = imageGap
                }
            })
        }
    }
}
