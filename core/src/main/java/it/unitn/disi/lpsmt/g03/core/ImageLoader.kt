package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ImageLoader {

    private val requestOptions = RequestOptions().transform(FitCenter(),
        RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            8f,
            Resources.getSystem().displayMetrics).toInt()))

    fun setCoverImageFromImage(
        uri: Uri?,
        glide: RequestManager,
        viewToSet: ImageView,
        errorImage: Int = R.drawable.baseline_broken_image_24,
    ) {
        glide.load(uri)
            .apply(requestOptions)
            .fallback(errorImage)
            .into(viewToSet)
    }

    fun setCoverImageFromCbz(uri: Uri?,
        contentResolver: ContentResolver,
        glide: RequestManager,
        viewToSet: ImageView,
        pageNum: Int = 0,
        errorImage: Int = R.drawable.baseline_broken_image_24) {
        if (uri == null) return

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(uri)
                    val zipInputStream = ZipInputStream(inputStream)
                    var zipEntry: ZipEntry? = zipInputStream.nextEntry
                    for (_page in 0..pageNum) zipInputStream.nextEntry
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
                                    .apply(requestOptions)
                                    .fallback(errorImage)
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