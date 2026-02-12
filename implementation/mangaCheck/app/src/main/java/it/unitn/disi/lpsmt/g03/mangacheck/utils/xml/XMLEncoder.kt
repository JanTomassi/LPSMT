package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.content.Context
import android.util.Log
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.ReadingListFragment
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLEncoder(private val context: Context) {

    // Manipulate the XML to add a new entry given the nav args
    fun addEntry(
        mangaId: Int,
        mangaName: String,
        mangaList: String,
        mangaImageBase64: String
    ) {
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

        val mangaListInXml = doc.createElement("list")
        mangaListInXml.textContent = mangaList

        newManga.appendChild(newTitle)
        newManga.appendChild(newId)
        newManga.appendChild(newImage)
        newManga.appendChild(mangaListInXml)

        parentElement.appendChild(newManga)


        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.XML_file)))
        )

        Log.v(
            ReadingListFragment::class.simpleName,
            context.applicationContext!!.openFileInput(context.getString(R.string.XML_file))
                .bufferedReader().readText()
        )
    }
}