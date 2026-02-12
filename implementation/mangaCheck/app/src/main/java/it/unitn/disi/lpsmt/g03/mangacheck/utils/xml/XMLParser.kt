package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.util.Log
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
    fun parseComics(xmlFile: File): MutableList<MangaEntry> {
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

    // Return a list of library entry representing the XML
    fun parseLibrary(xmlFile: File): MutableList<LibraryEntry> {
        val listToReturn: MutableList<LibraryEntry> = mutableListOf()
        val xmlDocument: Document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDocument.documentElement.normalize()

        val listOfLibraries: NodeList = xmlDocument.getElementsByTagName("library")

        if (listOfLibraries.length > 0) {

            for (index in 0 until listOfLibraries.length) {
                val element = listOfLibraries.item(index) as Element
                Log.e(XMLParser::class.simpleName,element.getElementsByTagName("title").item(0).textContent)
                listToReturn.add(
                    LibraryEntry(
                        element.getElementsByTagName("title").item(0).textContent,
                        element.getElementsByTagName("id").item(0).textContent.toInt(),
                        element.getElementsByTagName("image").item(0).textContent
                    )
                )
            }
        }
        return listToReturn
    }

    //Check if a whatSearch is already in the Xml, based on the title
    fun mangaAlreadyInList(xmlFile : File, mangaName : String, whatSearch : String) : Boolean{
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(xmlFile)

        val rawList = doc.getElementsByTagName(whatSearch)//.item(0)
            ?: return false

        for (index in 0 until rawList.length){
            val selectedManga : Element = rawList.item(index) as Element
            if(selectedManga.getElementsByTagName("title").item(0).textContent == mangaName){
                return true
            }
        }

        return false
    }
}