package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object CbzLoadImage {
    fun setCoverImage(uri: Uri?,
        contentResolver: ContentResolver,
        glide: RequestManager,
        viewToSet: ImageView) {
        if (uri == null) return

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(uri)
                    val zipInputStream = ZipInputStream(inputStream)
                    var zipEntry: ZipEntry? = zipInputStream.nextEntry
                    while (zipEntry != null) {
                        if (zipEntry.isDirectory) {
                            zipEntry = zipInputStream.nextEntry; continue
                        }
                        if (zipEntry.name.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE))) {
                            val image = zipInputStream.readBytes()
                            withContext(Dispatchers.Main) {
                                glide.load(image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .skipMemoryCache(false)
                                    .into(viewToSet)
                            }
                            return@withContext
                        }
                    }
                } catch (e: FileNotFoundException) {
                    Log.e(this::class.simpleName, e.toString())
                } finally {
                    inputStream?.close()
                }
            }
        }
    }
}