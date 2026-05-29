package com.example.myapplication.data

import android.content.ContentValues
import com.example.myapplication.model.Scenery

object HometownData {
    val sceneries = listOf(
        Scenery("鼋头渚", "太湖风光精华所在，春季樱花尤为知名。", "自然", "90元", true),
        Scenery("灵山胜境", "集佛教文化、山水景观与大型建筑于一体。", "人文", "210元", true),
        Scenery("惠山古镇", "祠堂群、古街巷和惠山泥人文化集中展示。", "人文", "免费", true),
        Scenery("南长街", "古运河畔历史街区，夜景和小吃很受欢迎。", "休闲", "免费", true),
        Scenery("蠡园", "依太湖而建的江南园林，湖景与亭廊相映。", "休闲", "45元", true),
        Scenery("梅园", "以梅花闻名的园林景区，冬春赏梅最佳。", "自然", "60元", true)
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
