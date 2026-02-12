package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import kotlin.text.*

class XMLParser {

    // Original code at:
    // https://developer.android.com/training/basics/network-ops/xml

    // Specify the namespace of the various attributes
    private val nameSpace: String? = null

    //Wrapper for the next function, it manage the file stream
    fun parse(inputStream: InputStream): List<Entry> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, Charsets.UTF_8.toString())
            parser.nextTag()
            return readComicsList(parser)
        }
    }

    // Initialise the list of entries
    // skip useless field (None is useless right now)
    private fun readComicsList(parser: XmlPullParser): List<Entry> {
        val entries = mutableListOf<Entry>()

        parser.require(XmlPullParser.START_TAG, nameSpace, "lists")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag.
            if (Regex(".+_list").containsMatchIn(parser.name)) {
                entries.add(readEntry(parser, parser.name))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth : Int = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    //Read teh fields of an entry comic
    private fun readEntry(parser: XmlPullParser, currentList: String): Entry {
        try {
            parser.require(XmlPullParser.START_TAG, nameSpace, "comic")
        }
        catch (e : Exception){
            return Entry("", null,null,null)
        }
        var title: String? = null
        var id: Int? = null
        var image: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readString(parser,"title")
                "id" -> id = readInt(parser,"id")
                "image" -> image = readString(parser,"image")
                else -> skip(parser)
            }
        }
        return Entry(currentList,title, id, image)
    }

    private fun readString(parser: XmlPullParser, fieldName: String): String {
            parser.require(XmlPullParser.START_TAG, nameSpace, fieldName)
            val fieldValue = readText(parser)
            parser.require(XmlPullParser.END_TAG, nameSpace, fieldName)
            return fieldValue
    }

    private fun readInt(parser: XmlPullParser, fieldName: String): Int {
        parser.require(XmlPullParser.START_TAG, nameSpace, fieldName)
        val fieldValue = readText(parser).toInt()
        parser.require(XmlPullParser.END_TAG, nameSpace, fieldName)
        return fieldValue
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}