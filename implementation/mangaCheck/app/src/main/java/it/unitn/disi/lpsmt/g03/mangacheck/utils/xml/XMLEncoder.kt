package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

/**
 * Interface for the various implementation of the Encoder
 */
interface XMLEncoder<T>{

    fun addEntry(entry : T)

    fun removeEntry(entry : T)

    fun modifyEntry(entry : T, fieldName : String, newValue: String)
}