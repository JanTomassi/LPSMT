package it.unitn.disi.lpsmt.g03.core

import android.content.ContentResolver
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.RequestManager
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

    fun getPagesInCbz(uri: Uri?, contentResolver: ContentResolver): Int {
        if (uri == null) return 0
        val zipInputStream = ZipInputStream(contentResolver.openInputStream(uri))
        var pages = 0
        var zipEntry: ZipEntry?

        try {
            zipEntry = zipInputStream.nextEntry
        } catch (e: ZipException) {
            return pages
        } catch (e: IOException) {
            return pages
        }

        while (zipEntry != null) {
            if (zipEntry.isDirectory) continue
            if (zipEntry.name.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE))) pages += 1
            zipEntry = zipInputStream.nextEntry
        }
        return pages
    }

    suspend fun setImageFromCbz(uri: Uri?,
        contentResolver: ContentResolver,
        glide: RequestManager,
        viewToSet: ImageView,
        pageNum: Int = 0,
        errorImage: Int = R.drawable.baseline_broken_image_24) {
        if (uri == null) return

        withContext(Dispatchers.IO) {
            val circularProgressDrawable = CircularProgressDrawable(viewToSet.context).apply {
                strokeWidth = viewToSet.width / 30.0f
                centerRadius = viewToSet.width / 10.0f

                val typedValue = TypedValue()
                val theme: Theme = viewToSet.context.theme
                theme.resolveAttribute(com.google.android.material.R.attr.colorOutline,
                    typedValue,
                    true)
                @ColorInt val color = typedValue.data

                setColorSchemeColors(color)
            }

            withContext(Dispatchers.Main) {
                circularProgressDrawable.start()
                glide.load(circularProgressDrawable).into(viewToSet)
            }
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
            } finally {
                zipInputStream?.close()
            }
        }
    }
}