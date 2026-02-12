package it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml

import android.content.Context
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLEncoder(private val context: Context) : XMLEncoder<ChapterEntry> {

    private val file: (ChapterEntry) -> File =
        { entry: ChapterEntry -> File("${context.filesDir}/${entry.id}/${context.getString(R.string.chapter_XML)}") }

    override fun addEntry(entry: ChapterEntry) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(file(entry).inputStream())
        val parentElement: Element = doc.getElementsByTagName("chapters").item(0) as Element

        val newChapter = doc.createElement("chapter")

        val newTitle = doc.createElement("title")
        newTitle.textContent = entry.title

        val newNum = doc.createElement("num")
        newNum.textContent = entry.num.toString()

        val newId = doc.createElement("id")
        newId.textContent = entry.id.toString()

        newChapter.appendChild(newTitle)
        newChapter.appendChild(newNum)
        newChapter.appendChild(newId)

        parentElement.appendChild(newChapter)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc), StreamResult(file(entry).outputStream())
        )
    }

    // Remove a selected entry from the library list file
    override fun removeEntry(entry: ChapterEntry) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(file(entry).inputStream())
        val parentElement: NodeList = doc.getElementsByTagName("chapter")

        for (index in 0 until parentElement.length) {
            val element = parentElement.item(index) as Element
            if (element.getElementsByTagName("num").item(0).textContent == entry.id.toString()) {
                element.parentNode.removeChild(element)
            }
        }
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc), StreamResult(file(entry).outputStream())
        )
    }

    override fun modifyEntry(entry: ChapterEntry, fieldName: String, newValue: String) {
        TODO("Not yet implemented")
    }
}