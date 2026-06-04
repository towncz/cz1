package com.example.myapplication.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.HometownData
import com.example.myapplication.data.SceneryContract

class SceneryDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    SceneryContract.DATABASE_NAME,
    null,
    SceneryContract.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${SceneryContract.TABLE_SCENERIES} (
                ${SceneryContract.SceneryColumns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${SceneryContract.SceneryColumns.NAME} TEXT NOT NULL,
                ${SceneryContract.SceneryColumns.INTRO} TEXT NOT NULL,
                ${SceneryContract.SceneryColumns.TYPE} TEXT NOT NULL,
                ${SceneryContract.SceneryColumns.PRICE} TEXT NOT NULL,
                ${SceneryContract.SceneryColumns.IS_OPEN} INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )

        HometownData.sceneries.forEach {
            db.insert(SceneryContract.TABLE_SCENERIES, null, HometownData.toContentValues(it))
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${SceneryContract.TABLE_SCENERIES}")
        onCreate(db)
    }
}
