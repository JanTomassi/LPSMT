package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

/**
 * Interface for the various implementation of the Encoder
 */
interface XMLEncoder<T> {

    /**
     * Add an entry to the class dependent xml file
     */
    fun addEntry(entry: T)

    /**
     * Remove an entry to the class dependent xml file
     */
    fun removeEntry(entry: T)

    /**
     * Modify an entry to the class dependent xml file
     */
    fun modifyEntry(entry: T, fieldName: String, newValue: String)
}