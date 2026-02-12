package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

/**
 * Interface for the various implementation of the Parser
 */
interface XMLParser<T> {
    /**
     * Parse the class dependent xml file
     */
    fun parse(): MutableList<T>

    /**
     * Check if [entry] is already in the xml
     */
    fun alreadyInList(entry: T): Boolean
}