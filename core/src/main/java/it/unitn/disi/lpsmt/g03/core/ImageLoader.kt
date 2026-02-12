package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

object ImageLoader {

    private val requestOptions = RequestOptions().transform(FitCenter(),
        RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            8f,
            Resources.getSystem().displayMetrics).toInt()))

    fun setCoverImageFromImage(
        uri: Uri?,
        viewToSet: ImageView,
        errorImage: Int = R.drawable.baseline_broken_image_24,
    ) {
        Glide.with(viewToSet).load(uri).apply(requestOptions).fallback(errorImage).into(viewToSet)
    }

    suspend fun getPagesInCbz(uri: Uri?,
        contentResolver: ContentResolver,
        progress: MutableLiveData<Int>): Int {
        if (uri == null) return 0
        val inputStream = contentResolver.openInputStream(uri)
        val zipInputStream = ZipInputStream(inputStream)
        var pages = 0
        var zipEntry: ZipEntry?
        val fileSize = uri.size(contentResolver)
        var byteRead: Long = 0

        try {
            zipEntry = zipInputStream.nextEntry
        } catch (e: ZipException) {
            return pages
        } catch (e: IOException) {
            return pages
        }

        while (zipEntry != null) {
            byteRead += zipEntry.size
            if (zipEntry.isDirectory) continue
            else if (zipEntry.name.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE))) {
                pages += 1

                progress.postValue(((byteRead / fileSize.toFloat()) * 1000.0f).toInt())

            }
            zipEntry = zipInputStream.nextEntry
        }
        withContext(Dispatchers.IO) {
            zipInputStream.close()
        }
        progress.postValue(1000)
        return pages
    }

    suspend fun setImageFromCbzUri(uri: Uri?,
        contentResolver: ContentResolver,
        viewToSet: ImageView,
        pageNum: Int = 0,
        errorImage: Int = R.drawable.baseline_broken_image_24) {
        if (uri == null) return

        withContext(Dispatchers.IO) {
            var zipInputStream: ZipInputStream? = null
            try {
                zipInputStream = ZipInputStream(contentResolver.openInputStream(uri))
                var zipEntry: ZipEntry? = zipInputStream.nextEntry
                for (_page in 0 until pageNum) zipInputStream.nextEntry
                if (zipEntry != null) {
                    while (zipEntry?.isDirectory == true) {
                        zipEntry = zipInputStream.nextEntry
                    }
                    if (zipEntry?.name?.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE)) == true) {
                        val image = zipInputStream.readBytes()
                        withContext(Dispatchers.Main) {
                            Glide.with(viewToSet)
                                .load(image)
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
            } finally {
                zipInputStream?.close()
            }
        }
    }

    suspend fun setImageFromCbzFile(file: ZipFile,
        fileEntries: List<ZipEntry>,
        viewToSet: ImageView,
        pageNum: Int = 0) {
        withContext(Dispatchers.IO) {
            val image = file.getInputStream(fileEntries[pageNum]).readBytes()
            withContext(Dispatchers.Main) {
                Glide.with(viewToSet).load(image).apply(requestOptions).into(viewToSet)
            }
        }
    }
}