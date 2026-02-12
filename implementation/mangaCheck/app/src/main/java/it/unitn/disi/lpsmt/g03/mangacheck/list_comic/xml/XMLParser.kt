package it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml

import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Example of the chapter xml
 * ```
 * <chapters>
 *   <chapter>
 *     <id>30001</id>
 *     <title>Berserk</title>
 *     <num>1</num>
 *   </chapter>
 *   ...
 * </chapters>
 * ```
 */

class XMLParser : XMLParser<ChapterEntry> {
    // Return a list of library entry representing the XML
    override fun parse(xmlFile: File): MutableList<ChapterEntry> {
        val listToReturn: MutableList<ChapterEntry> = mutableListOf()
        val xmlDocument: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDocument.documentElement.normalize()

        val listOfLibraries: NodeList = xmlDocument.getElementsByTagName("chapter")

        if (listOfLibraries.length > 0) {
            for (index in 0 until listOfLibraries.length) {
                val element = listOfLibraries.item(index) as Element
                listToReturn.add(
                    ChapterEntry(
                        element.getElementsByTagName("id").item(0).textContent.toInt(),
                        element.getElementsByTagName("num").item(0).textContent.toInt(),
                        element.getElementsByTagName("title").item(0).textContent
                    )
                )
            }
        }
        return listToReturn
    }

    //Check if a whatSearch is already in the Xml, based on the title
    override fun alreadyInList(xmlFile: File, entry: ChapterEntry): Boolean {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(xmlFile)

        val rawList = doc.getElementsByTagName("chapter") ?: return false

        for (index in 0 until rawList.length) {
            val selectedManga: Element = rawList.item(index) as Element
            if (selectedManga.getElementsByTagName("num").item(0).textContent == entry.num.toString()) {
                return true
            }
        }

        return false
    }
}