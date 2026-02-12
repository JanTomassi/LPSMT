package it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml

import android.content.Context
import android.util.Xml
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Example of the chapter xml
 * ```
 * <chapters>
 *   <chapter>
 *     <num>1</num>
 *     <title>Berserk</title>
 *   </chapter>
 *   ...
 * </chapters>
 * ```
 */

class XMLParser(id: Int, context: Context) {

    private val file: File = File("${context.filesDir}/$id/${context.getString(R.string.chapter_XML)}")

    /**
     * Init function, create the XML file if doesn't exist
     */
    init {
        if (!file.exists()) {
            file.createNewFile()
            val outputFile: FileOutputStream = file.outputStream()
            val serializer = Xml.newSerializer()
            serializer.setOutput(outputFile, "UTF-8")
            serializer.startDocument("UTF-8", true)

            serializer.startTag(null, "chapters")

            serializer.endTag(null, "chapters")

            serializer.endDocument()
            serializer.flush()

            outputFile.flush()
            outputFile.close()
        }
    }

    /**
     * Return a list of chapter entry representing the XML and order by chapter number
     * @return formatted list of ChapterEntry
     */
    fun parse(): MutableList<ChapterEntry> {
        val listToReturn: MutableList<ChapterEntry> = mutableListOf()
        val xmlDocument: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        xmlDocument.documentElement.normalize()

        val listOfLibraries: NodeList = xmlDocument.getElementsByTagName("chapter")

        if (listOfLibraries.length > 0) {
            for (index in 0 until listOfLibraries.length) {
                val element = listOfLibraries.item(index) as Element
                listToReturn.add(
                    ChapterEntry(
                        //element.getElementsByTagName("id").item(0).textContent.toInt(),
                        element.getElementsByTagName("num").item(0).textContent.toInt(),
                        element.getElementsByTagName("title").item(0).textContent
                    )
                )
            }
        }
        listToReturn.sortBy { it.num }
        return listToReturn
    }

    /**
     * Check if a [entry] is already in the Xml, based on the title
     * @param [entry] entry to look for
     */
    fun alreadyInList(entry: ChapterEntry): Boolean {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(file)

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