package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipException
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
        glide.load(uri).apply(requestOptions).fallback(errorImage).into(viewToSet)
    }

    tailrec fun getPageInCbz(uri: Uri?,
        contentResolver: ContentResolver,
        zipInputStream: ZipInputStream? = null,
        pages: Int = 0): Int {
        val startTime = System.currentTimeMillis()
        var mZipInputStream = zipInputStream
        if (uri == null) return 0
        if (mZipInputStream == null) mZipInputStream = ZipInputStream(contentResolver.openInputStream(
            uri))
        val endTime = System.currentTimeMillis()
        Log.v("getPageInCbz", "${endTime - startTime}ms, page: $pages")
        val zipEntry: ZipEntry?

        try {
            zipEntry = mZipInputStream.nextEntry
        } catch (e: ZipException) {
            return pages
        } catch (e: IOException) {
            return pages
        }

        when {
            zipEntry == null -> return pages
            zipEntry.isDirectory -> return getPageInCbz(uri,
                contentResolver,
                mZipInputStream,
                pages)

            zipEntry.name.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE)) -> return getPageInCbz(
                uri,
                contentResolver,
                mZipInputStream,
                pages + 1)
        }
        return getPageInCbz(uri, contentResolver, mZipInputStream, pages)
    }

    fun setImageFromCbz(uri: Uri?,
        contentResolver: ContentResolver,
        glide: RequestManager,
        viewToSet: ImageView,
        pageNum: Int = 0,
        errorImage: Int = R.drawable.baseline_broken_image_24) {
        if (uri == null) return

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    val zipInputStream = ZipInputStream(contentResolver.openInputStream(uri))
                    var zipEntry: ZipEntry? = zipInputStream.nextEntry
                    for (_page in 0 until pageNum) zipInputStream.nextEntry
                    while (zipEntry != null) {
                        if (zipEntry.isDirectory) {
                            zipEntry = zipInputStream.nextEntry; continue
                        }
                        if (zipEntry.name.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE))) {
                            val image = zipInputStream.readBytes()
                            withContext(Dispatchers.Main) {
                                glide.load(image)
                                    .signature(ObjectKey("$uri - $pageNum"))
                                    .apply(requestOptions)
                                    .fallback(errorImage)
                                    .into(viewToSet)
                            }
                            return@withContext
                        }
                    }
                } catch (e: FileNotFoundException) {
                    Log.e(this::class.simpleName, e.toString())
                }
            }
        }
    }
}