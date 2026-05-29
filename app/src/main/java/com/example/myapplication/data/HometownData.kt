package com.example.myapplication.data

import android.content.ContentValues
import com.example.myapplication.model.Scenery

object HometownData {
    val sceneries = listOf(
        Scenery("趵突泉", "济南三大名胜之一，泉水清澈灵动。", "自然", "40元", true),
        Scenery("大明湖", "湖光亭影相映，是济南经典游览地。", "休闲", "免费", true),
        Scenery("千佛山", "山中古迹众多，可俯瞰济南城区。", "人文", "30元", true),
        Scenery("黑虎泉", "泉群声势有力，临河风景开阔。", "自然", "免费", true),
        Scenery("山东博物馆", "展示齐鲁历史文化与珍贵文物。", "人文", "免费", true),
        Scenery("宽厚里", "街区汇集小吃、文创与夜间休闲。", "休闲", "免费", true)
    )

    fun toContentValues(scenery: Scenery): ContentValues = ContentValues().apply {
        put(SceneryContract.SceneryColumns.NAME, scenery.name)
        put(SceneryContract.SceneryColumns.INTRO, scenery.intro)
        put(SceneryContract.SceneryColumns.TYPE, scenery.type)
        put(SceneryContract.SceneryColumns.PRICE, scenery.price)
        put(SceneryContract.SceneryColumns.IS_OPEN, if (scenery.isOpen) 1 else 0)
    }

    fun createSceneryValues(
        name: String,
        intro: String,
        type: String,
        price: String,
        isOpen: Boolean
    ): ContentValues = toContentValues(Scenery(name, intro, type, price, isOpen))
}
