package com.example.myapplication.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.myapplication.data.SceneryContract

class SceneryProvider : ContentProvider() {
    private lateinit var dbHelper: SceneryDBHelper

    override fun onCreate(): Boolean {
        dbHelper = SceneryDBHelper(requireNotNull(context))
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val db = dbHelper.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            SceneryContract.ALL_SCENERIES -> db.query(
                SceneryContract.TABLE_SCENERIES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder ?: "${SceneryContract.SceneryColumns.ID} ASC"
            )
            SceneryContract.ONE_SCENERY -> db.query(
                SceneryContract.TABLE_SCENERIES,
                projection,
                "${SceneryContract.SceneryColumns.ID}=?",
                arrayOf(ContentUris.parseId(uri).toString()),
                null,
                null,
                sortOrder
            )
            else -> throwInvalidUri(uri)
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        if (uriMatcher.match(uri) != SceneryContract.ALL_SCENERIES) {
            throwInvalidUri(uri)
        }

        val id = dbHelper.writableDatabase.insert(
            SceneryContract.TABLE_SCENERIES,
            null,
            values ?: ContentValues()
        )
        val newUri = ContentUris.withAppendedId(SceneryContract.CONTENT_URI, id)
        context?.contentResolver?.notifyChange(newUri, null)
        Log.d(TAG, "insert scenery, id=$id")
        return newUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val count = when (uriMatcher.match(uri)) {
            SceneryContract.ALL_SCENERIES -> dbHelper.writableDatabase.delete(
                SceneryContract.TABLE_SCENERIES,
                selection,
                selectionArgs
            )
            SceneryContract.ONE_SCENERY -> dbHelper.writableDatabase.delete(
                SceneryContract.TABLE_SCENERIES,
                "${SceneryContract.SceneryColumns.ID}=?",
                arrayOf(ContentUris.parseId(uri).toString())
            )
            else -> throwInvalidUri(uri)
        }

        context?.contentResolver?.notifyChange(uri, null)
        Log.d(TAG, "delete scenery count=$count, uri=$uri")
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val count = when (uriMatcher.match(uri)) {
            SceneryContract.ALL_SCENERIES -> dbHelper.writableDatabase.update(
                SceneryContract.TABLE_SCENERIES,
                values,
                selection,
                selectionArgs
            )
            SceneryContract.ONE_SCENERY -> dbHelper.writableDatabase.update(
                SceneryContract.TABLE_SCENERIES,
                values,
                "${SceneryContract.SceneryColumns.ID}=?",
                arrayOf(ContentUris.parseId(uri).toString())
            )
            else -> throwInvalidUri(uri)
        }

        context?.contentResolver?.notifyChange(uri, null)
        Log.d(TAG, "update scenery count=$count, uri=$uri")
        return count
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            SceneryContract.ALL_SCENERIES -> "vnd.android.cursor.dir/vnd.${SceneryContract.AUTHORITY}.scenery"
            SceneryContract.ONE_SCENERY -> "vnd.android.cursor.item/vnd.${SceneryContract.AUTHORITY}.scenery"
            else -> throwInvalidUri(uri)
        }
    }

    private fun throwInvalidUri(uri: Uri): Nothing {
        Log.e(TAG, "Illegal uri: $uri")
        throw IllegalArgumentException("Unsupported scenery uri: $uri")
    }

    companion object {
        private const val TAG = "SceneryProvider"

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(SceneryContract.AUTHORITY, "sceneries", SceneryContract.ALL_SCENERIES)
            addURI(SceneryContract.AUTHORITY, "sceneries/#", SceneryContract.ONE_SCENERY)
        }
    }
}
