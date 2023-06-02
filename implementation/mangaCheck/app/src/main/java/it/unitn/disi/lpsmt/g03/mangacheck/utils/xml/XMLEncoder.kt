package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.content.Context
import android.util.Log
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.ReadingListFragment
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLEncoder(private val context: Context) {

    fun addEntry(
        mangaId: Int,
        mangaName: String,
        mangaList: String,
        mangaImageBase64: String
    ): Unit {
//        Log.v(XMLEncoder::class.simpleName, mangaList)
//        Log.v(XMLEncoder::class.simpleName, mangaId.toString())
//        Log.v(XMLEncoder::class.simpleName, mangaName)
//        Log.v(XMLEncoder::class.simpleName, mangaImageBase64)
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.XML_file)))
        val parentElement: Element = doc.getElementsByTagName(mangaList).item(0) as Element

        val newManga = doc.createElement("comic")

        val newTitle = doc.createElement("title")
        newTitle.textContent = mangaName

        val newId = doc.createElement("id")
        newId.textContent = mangaId.toString()

        val newImage = doc.createElement("image")
        newImage.textContent = mangaImageBase64

        newManga.appendChild(newTitle)
        newManga.appendChild(newId)
        newManga.appendChild(newImage)

        parentElement.appendChild(newManga)


        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.XML_file)))
        )

        Log.e(
            ReadingListFragment::class.simpleName,
            context.applicationContext!!.openFileInput(context.getString(R.string.XML_file))
                .bufferedReader().readText()
        )
    }
}