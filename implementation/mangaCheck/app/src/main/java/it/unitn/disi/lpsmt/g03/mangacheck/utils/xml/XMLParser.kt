package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

/**
 * Interface for the various implementation of the Parser
 */
interface XMLParser<T> {
    fun parse(): MutableList<T>

    fun alreadyInList(entry: T): Boolean
}