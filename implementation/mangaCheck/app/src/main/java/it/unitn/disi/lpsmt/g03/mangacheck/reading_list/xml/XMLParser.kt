package it.unitn.disi.lpsmt.g03.mangacheck.reading_list.xml

import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.MangaEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class XMLParser : XMLParser<MangaEntry> {
    // Divide the XML in a list of MangaEntry
    override fun parse(xmlFile: File): MutableList<MangaEntry> {
        val listToReturn: MutableList<MangaEntry> = mutableListOf()
        val xmlDocument: Document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDocument.documentElement.normalize()

        val listOfMangas: NodeList = xmlDocument.getElementsByTagName("comic")

        if (listOfMangas.length > 0) {

            for (index in 0 until listOfMangas.length) {
                val element = listOfMangas.item(index) as Element
                listToReturn.add(
                    MangaEntry(
                        element.getElementsByTagName("list").item(0).textContent,
                        element.getElementsByTagName("title").item(0).textContent,
                        element.getElementsByTagName("id").item(0).textContent.toInt(),
                        element.getElementsByTagName("description").item(0).textContent
                    )
                )
            }
        }
        return listToReturn
    }

    //Check if a whatSearch is already in the Xml, based on the title
    override fun alreadyInList(xmlFile : File, entry: MangaEntry) : Boolean{
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(xmlFile)

        val rawList = doc.getElementsByTagName("comic")
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