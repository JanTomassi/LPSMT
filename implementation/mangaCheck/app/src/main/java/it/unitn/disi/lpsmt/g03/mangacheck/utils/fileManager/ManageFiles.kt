package it.unitn.disi.lpsmt.g03.mangacheck.utils.fileManager

import android.content.Context
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import java.io.File

class ManageFiles(private val context: Context){

    /**
     * Create library folder to contains chapter associated with that library
     * @param [library] LibraryEntry to associate with a directory
     */
    fun createLibraryFolder(library: LibraryEntry){
        val libraryDir = File(context.filesDir, library.id.toString())
        if(!libraryDir.isDirectory){
            libraryDir.mkdir()
        }
    }

    /**
     * Delete the files associated ith a library
     * @param [library] entry associated with folder to delete
     */
    fun deleteLibraryFolder(library: LibraryEntry){
        val libraryDir = File(context.filesDir, library.id.toString())
        if (libraryDir.isDirectory){
            libraryDir.deleteRecursively()
        }
    }

    /**
     * Create cache folder for cover images
     */
    fun createImageCacheFolder(){
        val imageCache = File(context.cacheDir, "image")
        if(!imageCache.isDirectory){
            imageCache.mkdir()
        }
    }
}