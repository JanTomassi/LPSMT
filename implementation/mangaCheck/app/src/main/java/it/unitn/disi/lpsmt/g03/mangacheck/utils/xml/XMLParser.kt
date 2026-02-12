package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/*
  The structure of the XML:
    <comics>
        <comic>
            <id/>
            <title/>
            <image/>
            <list/>
            <description/>
        <comic/>
        .
        .
    <comics/>
   Any list can contain n different manga
 */

class XMLParser {

    // Divide the XML in a list of MangaEntry
    fun parse(xmlFile: File): MutableList<MangaEntry> {
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
                        element.getElementsByTagName("image").item(0).textContent,
                        element.getElementsByTagName("description").item(0).textContent
                    )
                )
            }
        }
        return listToReturn
    }

    fun mangaAlreadyInList(xmlFile : File, mangaName : String, whatSearch : String) : Boolean{
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(xmlFile)

        val rawlist = doc.getElementsByTagName(whatSearch).item(0)
            ?: return false

        val listOfAllComics : Element = rawlist as Element

        val mangaToFind: Element? = listOfAllComics.takeIf {
            it.getElementsByTagName("title").item(0).textContent == mangaName
        }
        return (null != mangaToFind)
    }
}