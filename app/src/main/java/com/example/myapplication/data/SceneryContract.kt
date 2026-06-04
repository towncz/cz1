package com.example.myapplication.data

import android.net.Uri
import android.provider.BaseColumns

object SceneryContract {
    const val AUTHORITY = "com.chenzhen.hometown"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/sceneries")

    const val ALL_SCENERIES = 1
    const val ONE_SCENERY = 2

    const val DATABASE_NAME = "hometown.db"
    const val DATABASE_VERSION = 3
    const val TABLE_SCENERIES = "sceneries"

    object SceneryColumns : BaseColumns {
        const val ID = "id"
        const val NAME = "name"
        const val INTRO = "intro"
        const val TYPE = "type"
        const val PRICE = "price"
        const val IS_OPEN = "is_open"
    }
}
