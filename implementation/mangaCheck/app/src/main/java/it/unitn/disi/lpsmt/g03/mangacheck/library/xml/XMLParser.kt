package it.unitn.disi.lpsmt.g03.mangacheck.library.xml

import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * <libraries>
 *    <library>
 *       <title....
 *    </library>
 *    .
 *    .
 *    .
 * </libraries>
 */

class XMLParser : XMLParser<LibraryEntry> {
    // Return a list of library entry representing the XML
    override fun parse(xmlFile: File): MutableList<LibraryEntry> {
        val listToReturn: MutableList<LibraryEntry> = mutableListOf()
        val xmlDocument: Document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDocument.documentElement.normalize()

        val listOfLibraries: NodeList = xmlDocument.getElementsByTagName("library")

        if (listOfLibraries.length > 0) {

            for (index in 0 until listOfLibraries.length) {
                val element = listOfLibraries.item(index) as Element
                listToReturn.add(
                    LibraryEntry(
                        element.getElementsByTagName("title").item(0).textContent,
                        element.getElementsByTagName("id").item(0).textContent.toInt()
                    )
                )
            }
        }
        return listToReturn
    }

    //Check if a whatSearch is already in the Xml, based on the title
    override fun alreadyInList(xmlFile : File, entry: LibraryEntry) : Boolean{
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(xmlFile)

        val rawList = doc.getElementsByTagName("library")
            ?: return false

        for (index in 0 until rawList.length){
            val selectedManga : Element = rawList.item(index) as Element
            if(selectedManga.getElementsByTagName("id").item(0).textContent == entry.id.toString()){
                return true
            }
        }

        return false
    }
}