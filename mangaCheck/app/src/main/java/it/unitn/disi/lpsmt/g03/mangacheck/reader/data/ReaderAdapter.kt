package it.unitn.disi.lpsmt.g03.mangacheck.reader.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import java.io.BufferedInputStream
import java.io.File
import java.util.LinkedList
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

internal class ReaderAdapter(private val uri: Uri, private val context: Context) {

    private val imageList: LinkedList<String> = imageViewInit()

    fun getCount(): Int {
        return imageList.size
    }

    fun getItem(position: Int): Any {
        return imageList[position]
    }

    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * In this case the position is the manga ID, the id is negative only if the button is the
     * error message so no event on click set
     */
    fun getView(page: Int, view: View?, width: Int, height: Int): View {
        var convertView: ImageView? = view as ImageView?

        if (convertView == null) {
            val im = ImageView(context)
            convertView = im
        }
        var bitmap = BitmapFactory.decodeFile(imageList[page])
        val size = getBestSize(bitmap.width / bitmap.height.toFloat(), width, height)
        bitmap = Bitmap.createScaledBitmap(bitmap, size.width, size.height, true)
        convertView.setImageBitmap(bitmap)

        return convertView
    }

    private fun getBestSize(aspectRatio: Float, width: Int, height: Int): Size {
        return if (aspectRatio <= 1) Size(
            (height * aspectRatio).toInt(), (width / aspectRatio).toInt()
        ) else Size((width * aspectRatio).toInt(), (height / aspectRatio).toInt())
    }

    private fun imageViewInit(): LinkedList<String> {
        val res: LinkedList<String> = LinkedList()
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))

        var entry: ZipEntry? = zipInputStream.nextEntry
        var index = 0

        while (entry != null) {
            val zipByteArray = zipInputStream.readBytes()

            File.createTempFile("bitmap-$index", null, context.cacheDir)
            val cacheFile = File(context.cacheDir, "bitmap-$index")

            cacheFile.writeBytes(zipByteArray)

            res.add(cacheFile.path)

            Log.v(TAG, "We are at page: $index. the entry is named ${entry.name} \tand the size is ${entry.size}")

            index++
            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }

        Log.v(TAG, "Ended at page: $index")
        zipInputStream.close()
        return res
    }

    companion object {
        val TAG = ReaderAdapter::class.simpleName
    }
}