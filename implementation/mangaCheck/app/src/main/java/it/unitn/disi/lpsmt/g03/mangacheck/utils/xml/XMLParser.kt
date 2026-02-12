package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

interface XMLParser<T> {
    fun parse(): MutableList<T>

    fun alreadyInList(entry: T): Boolean
}