package it.unitn.disi.lpsmt.g03.mangacheck.utils.fileManager

import android.content.Context
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import java.io.File

class ManageFiles(private val context: Context){

    // If necessary creates the directory to store files associated with a library
    fun createLibraryFolder(library: LibraryEntry){
        val libraryDir = File(context.filesDir, library.id.toString())
        if(!libraryDir.isDirectory){
            libraryDir.mkdir()
        }
    }

    // Delete the files associated ith a library
    fun deleteLibraryFolder(library: LibraryEntry){
        val libraryDir = File(context.filesDir, library.id.toString())
        if (libraryDir.isDirectory){
            libraryDir.deleteRecursively()
        }
    }

    fun createImageCacheFolder(){
        val imageCache = File(context.cacheDir, "image")
        if(!imageCache.isDirectory){
            imageCache.mkdir()
        }
    }
}