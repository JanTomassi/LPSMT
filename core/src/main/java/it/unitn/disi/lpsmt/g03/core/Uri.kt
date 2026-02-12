package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

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

suspend fun Uri.copyToLocalStorage(context: Context,
    contentResolver: ContentResolver?,
    progress: MutableLiveData<Int>): File {
    return withContext(Dispatchers.IO) {
        val uriSize = this@copyToLocalStorage.size(contentResolver)
        val inStream: InputStream? = contentResolver?.openInputStream(this@copyToLocalStorage)
        val outStream: OutputStream = File(context.cacheDir, "tmp_copy").outputStream()

        inStream?.let {
            try {
                var totalLen: Long = 0
                val buf = ByteArray(1024)
                var len: Int = inStream.read(buf)
                totalLen += len

                while (len > 0) {
                    outStream.write(buf, 0, len)
                    len = inStream.read(buf)
                    totalLen += len
                    progress.postValue(((totalLen.toFloat() / uriSize.toFloat()) * 1000.0f).toInt())
                }

            } catch (ie: IOException) {
                ie.printStackTrace()
            } finally {
                outStream.close()
                inStream.close()
            }
        }
        progress.postValue(1000)
        return@withContext File(context.cacheDir, "tmp_copy")
    }
}

fun Uri.size(contentResolver: ContentResolver?): Long {
    var result: Long
    if (!this.scheme.equals("content")) result = 0
    contentResolver?.query(this, arrayOf(OpenableColumns.SIZE), null, null).use { cur ->
        result = if (cur != null && cur.moveToFirst()) {
            cur.getLong(cur.getColumnIndexOrThrow(OpenableColumns.SIZE))
        } else 0
    }
    return result
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