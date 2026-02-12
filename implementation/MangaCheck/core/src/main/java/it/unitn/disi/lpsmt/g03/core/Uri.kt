package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Get the file name of this uri file
 */
fun Uri.getFileName(contentResolver: ContentResolver?): String {
    var result: String? = null
    if (this.scheme.equals("content")) {
        val cursor: Cursor? = contentResolver?.query(this,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null)
        cursor.use { cur ->
            if (cur != null && cur.moveToFirst()) {
                result = cur.getString(cur.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = this.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "No file selected"
}

/**
 * Test if this is a uri _content_ and if the extension of the file is _.cbz_ and if the size is grater then 0
 */
fun Uri.isCbz(contentResolver: ContentResolver?): Boolean {
    if (!this.scheme.equals("content")) return false
    val nameAndSize: Boolean = (contentResolver?.query(this,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null).use { cur ->
        if (cur != null && cur.moveToFirst()) {
            cur.getString(cur.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                .contains(".cbz$".toRegex()) && cur.getLong(cur.getColumnIndexOrThrow(
                OpenableColumns.SIZE)) > 0
        } else false
    })
    return nameAndSize
}