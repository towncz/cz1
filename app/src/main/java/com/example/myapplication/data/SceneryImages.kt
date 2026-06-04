package com.example.myapplication.data

import com.example.myapplication.R

object SceneryImages {
    val galleryImageResIds = listOf(
        R.drawable.yuantouzhu,
        R.drawable.linshanshengjin,
        R.drawable.huishanguzhen,
        R.drawable.nanchangjie,
        R.drawable.liyuan,
        R.drawable.meiyuan
    )

    fun imageForName(name: String): Int {
        return when {
            name.contains("鼋头渚") -> R.drawable.yuantouzhu
            name.contains("灵山") -> R.drawable.linshanshengjin
            name.contains("惠山") -> R.drawable.huishanguzhen
            name.contains("南长街") -> R.drawable.nanchangjie
            name.contains("蠡园") -> R.drawable.liyuan
            name.contains("梅园") -> R.drawable.meiyuan
            name.contains("清名桥") || name.contains("古运河") -> R.drawable.qingmingqiao
            else -> R.drawable.yuantouzhu
        }
    }
}
