package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.content.Context
import java.io.File

class ManageFiles(private val library: LibraryEntry, private val context: Context){

    // If necessary creates the directory to store files associated with a library
    fun createLibraryFolder(){
        val libraryDir = File(context.filesDir, library.id.toString())
        if(!libraryDir.isDirectory){
            libraryDir.mkdir()
        }
    }

    // Delete the files associated ith a library
    fun deleteDir(){
        val libraryDir = File(context.filesDir, library.id.toString())
        if (libraryDir.isDirectory){
            libraryDir.deleteRecursively()
        }
    }

}