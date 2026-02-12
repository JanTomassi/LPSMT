package it.unitn.disi.lpsmt.g03.mangacheck.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import it.unitn.disi.lpsmt.g03.mangacheck.utils.http.ServerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.io.File

class ImageManager {

    fun retrieveImage(context: Context, id: Int): Bitmap? {
        val imageFile = File(context.cacheDir, "image/$id")

        if (!imageFile.exists()) {
            CoroutineScope(Dispatchers.IO).launch {
                ServerRequest(context, id).queryImage()
            }
        }

        return BitmapFactory.decodeFile(imageFile.absolutePath)
    }
}