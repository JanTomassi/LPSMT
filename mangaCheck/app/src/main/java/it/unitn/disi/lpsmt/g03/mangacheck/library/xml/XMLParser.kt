package it.unitn.disi.lpsmt.g03.mangacheck.library.xml

import android.content.Context
import android.util.Log
import android.util.Xml
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * ```
 * <libraries>
 *    <library>
 *       <title></title>
 *       <id></id>
 *    </library>
 *    ...
 * </libraries>
 * ```
 */

class XMLParser(context: Context) : XMLParser<LibraryEntry> {

    private val libraryXMLFile = File(context.filesDir, context.getString(R.string.library_XML))

    init {
        if (!libraryXMLFile.exists()) {

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

    // Return a list of library entry representing the XML
    override fun parse(): MutableList<LibraryEntry> {
        val listToReturn: MutableList<LibraryEntry> = mutableListOf()
        val xmlDocument: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(libraryXMLFile)
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
    override fun alreadyInList(entry: LibraryEntry): Boolean {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(libraryXMLFile)

        val rawList = doc.getElementsByTagName("library") ?: return false

        for (index in 0 until rawList.length) {
            val selectedManga: Element = rawList.item(index) as Element
            if (selectedManga.getElementsByTagName("id").item(0).textContent == entry.id.toString()) {
                return true
            }
        }

        return false
    }
}