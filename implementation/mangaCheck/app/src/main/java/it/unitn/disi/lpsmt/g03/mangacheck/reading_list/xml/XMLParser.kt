package it.unitn.disi.lpsmt.g03.mangacheck.reading_list.xml

import android.content.Context
import android.util.Log
import android.util.Xml
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.MangaEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory

class XMLParser(context: Context) : XMLParser<MangaEntry> {
    private val readingXMLFile = File(context.filesDir, context.getString(R.string.XML_file))

    init {
        if (!readingXMLFile.exists()) {

            val outputFile: FileOutputStream =
                context.openFileOutput(context.getString(R.string.library_XML), Context.MODE_PRIVATE)

            val serializer = Xml.newSerializer()
            serializer.setOutput(outputFile, "UTF-8")
            serializer.startDocument("UTF-8", true)

            serializer.startTag(null, "libraries")

            serializer.endTag(null, "libraries")

            serializer.endDocument()
            serializer.flush()

            outputFile.flush()
            outputFile.close()
        }
        Log.v(
            XMLParser::class.simpleName,
            context.applicationContext!!.openFileInput(context.getString(R.string.library_XML)).bufferedReader()
                .readText()
        )
    }

    // Divide the XML in a list of MangaEntry
    override fun parse(): MutableList<MangaEntry> {
        val listToReturn: MutableList<MangaEntry> = mutableListOf()
        val xmlDocument: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(readingXMLFile)
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
    override fun alreadyInList(entry: MangaEntry): Boolean {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(readingXMLFile)

        val rawList = doc.getElementsByTagName("comic") ?: return false

        for (index in 0 until rawList.length) {
            val selectedManga: Element = rawList.item(index) as Element
            if (selectedManga.getElementsByTagName("id").item(0).textContent == entry.id.toString()) {
                return true
            }
        }

        return false
    }
}