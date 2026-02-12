package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import java.io.File

interface XMLParser<T>{
    fun parse(xmlFile: File): MutableList<T>

    fun alreadyInList(xmlFile : File, entry : T) : Boolean
}