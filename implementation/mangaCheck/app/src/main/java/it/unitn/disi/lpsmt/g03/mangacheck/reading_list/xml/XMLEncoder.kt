package it.unitn.disi.lpsmt.g03.mangacheck.reading_list.xml

import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import android.content.Context
import android.util.Log
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.ReadingListFragment
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.MangaEntry


class XMLEncoder(private val context: Context) : XMLEncoder<MangaEntry> {

    // Manipulate the XML to add a new entry given the nav args
    override fun addEntry(
        entry: MangaEntry
    ) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.XML_file)))
        val parentElement: Element = doc.getElementsByTagName("comics").item(0) as Element

        val newManga = doc.createElement("comic")

        val mangaListInXml = doc.createElement("list")
        mangaListInXml.textContent = entry.list

        val newTitle = doc.createElement("title")
        newTitle.textContent = entry.title

        val newId = doc.createElement("id")
        newId.textContent = entry.id.toString()

        //val newImage = doc.createElement("image")
        //newImage.textContent = mangaImageBase64

        val newDescription = doc.createElement("description")
        newDescription.textContent = entry.description

        newManga.appendChild(mangaListInXml)
        newManga.appendChild(newTitle)
        newManga.appendChild(newId)
        //newManga.appendChild(newImage)
        newManga.appendChild(newDescription)

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

    // Modify the list of a comic
    override fun modifyEntry(entry: MangaEntry,fieldName : String, newValue: String) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.XML_file)))

        // Find the comic to modify
        val listOfAllComics: NodeList = doc.getElementsByTagName("comic")

        for (index in 0 until listOfAllComics.length){
            val currentMangaEntry = listOfAllComics.item(index) as Element
            if(currentMangaEntry.getElementsByTagName("id").item(0).textContent == entry.id.toString()){
                currentMangaEntry.getElementsByTagName(fieldName).item(0).textContent = newValue
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.XML_file)))
        )
    }

    override fun removeEntry(entry: MangaEntry) {
        TODO("Not yet implemented")
    }
}