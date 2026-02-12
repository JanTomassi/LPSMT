package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class TestXMLParser {

    private fun extractMangaInAList(
        xml: Document,
        listToReturn: MutableList<Entry>,
        list: String
    ) {
        val root = xml.getElementsByTagName(list) as List<*>

        Log.v("Dio santo", root.size.toString())

        if (root.isNotEmpty()) {

            for (index in root.indices) {

                val mangaNode: Element = root[index] as Element
                listToReturn.add(
                    Entry(
                        list,
                        mangaNode.getElementsByTagName("title").item(0).textContent,
                        mangaNode.getElementsByTagName("id").item(0).textContent.toInt(),
                        mangaNode.getElementsByTagName("image").item(0).textContent
                    )
                )
            }
        }
    }

    fun testParse(xmlFile: File): MutableList<Entry> {
        val listToReturn: MutableList<Entry> = mutableListOf()
        val xmlDocument: Document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDocument.documentElement.normalize()

        val listOfLists: NodeList = xmlDocument.getElementsByTagName("lists")

        Log.v(TestXMLParser::class.simpleName, listOfLists.length.toString())

        if (listOfLists.length > 0) {
            extractMangaInAList(xmlDocument, listToReturn, "reading_list")

//            extractMangaInAList(xmlDocument, listToReturn, "completed_list")
//
//            for (element in listToReturn) {
//                Log.v(TestXMLParser::class.simpleName, element.title.toString())
//            }
//
//            extractMangaInAList(xmlDocument, listToReturn, "planning_list")
//
//            for (element in listToReturn) {
//                Log.v(TestXMLParser::class.simpleName, element.title.toString())
//            }
        }

        for (element in listToReturn) {
            Log.v(TestXMLParser::class.simpleName, element.title.toString())
        }

        return listToReturn
    }
}