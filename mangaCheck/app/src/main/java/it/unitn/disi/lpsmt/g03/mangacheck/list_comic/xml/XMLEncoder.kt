package it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml

import android.content.Context
import android.util.Xml
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

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

class XMLEncoder(id: Int, context: Context) : XMLEncoder<ChapterEntry> {

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
     * Add a new entry in the comic list file
     * @param [entry] the new entry to add
     */
    override fun addEntry(entry: ChapterEntry) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse(file.inputStream())
        val parentElement: Element = doc.getElementsByTagName("chapters").item(0) as Element

        val newChapter = doc.createElement("chapter")

        val newTitle = doc.createElement("title")
        newTitle.textContent = entry.title

        val newNum = doc.createElement("num")
        newNum.textContent = entry.num.toString()

        newChapter.appendChild(newTitle)
        newChapter.appendChild(newNum)

        parentElement.appendChild(newChapter)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc), StreamResult(file.outputStream())
        )
    }

    /**
     * Remove a selected entry from the comic list file
     * @param [entry] the entry to remove
     */
    override fun removeEntry(entry: ChapterEntry) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse(file.inputStream())
        val parentElement: NodeList = doc.getElementsByTagName("chapter")

        for (index in 0 until parentElement.length) {
            val element = parentElement.item(index) as Element
            if (element.getElementsByTagName("num").item(0).textContent == entry.num.toString()) {
                element.parentNode.removeChild(element)
            }
        }
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc), StreamResult(file.outputStream())
        )
    }

    /**
     * Function to modify an entry
     * @param [entry] the entry to modify
     * @param [fieldName] the field to modify
     * @param [newValue] tge new value to assign to [fieldName]
     */
    override fun modifyEntry(entry: ChapterEntry, fieldName: String, newValue: String) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse(file.inputStream())
        val elementList: NodeList = doc.getElementsByTagName("chapter")

        for (index in 0 until elementList.length) {
            val element = elementList.item(index) as Element
            if (element.getElementsByTagName("num").item(0).textContent == entry.num.toString()) {
                element.getElementsByTagName(fieldName).item(0).textContent = newValue
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc), StreamResult(file.outputStream())
        )
    }
}