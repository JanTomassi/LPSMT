package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/*
  The structure of the XML:
  <lists>
      <reading_list>
          <comic>
              <id/>
              <title/>
              <image/>
              <list/>
              <description/>
           <comic/>
           .
           .
       <reading_list/>
       <planning_list/>
       <completed_list/>
   <lists/>

   Any list can contain n different manga
 */

class XMLParser {

    // Divide the XML in a list of Entry
    fun parse(xmlFile: File): MutableList<Entry> {
        val listToReturn: MutableList<Entry> = mutableListOf()
        val xmlDocument: Document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
        xmlDocument.documentElement.normalize()

        val listOfMangas: NodeList = xmlDocument.getElementsByTagName("comic")

        if (listOfMangas.length > 0) {

            for (index in 0 until listOfMangas.length) {
                val element = listOfMangas.item(index) as Element
                listToReturn.add(
                    Entry(
                        element.getElementsByTagName("list").item(0).textContent,
                        element.getElementsByTagName("title").item(0).textContent,
                        element.getElementsByTagName("id").item(0).textContent.toInt(),
                        element.getElementsByTagName("image").item(0).textContent,
                        element.getElementsByTagName("description").item(0).textContent
                    )
                )
            }
        }
        return listToReturn
    }
}