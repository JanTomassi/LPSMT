package it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageView
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.info_reading.InfoReadingDialogFragment
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.Entry

internal class ReadingAdapter(
    private val comicsList : List<Entry>, private val context: Context
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null

    private lateinit var comicName: TextView
    private lateinit var chapterCounter: TextView
    private lateinit var circleImage: ImageView

    override fun getCount(): Int {
        return comicsList.size
    }

    override fun getItem(position: Int): Entry {
        return comicsList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        val comic : Entry = getItem(position)
        val comicImageBase64 = comic.image!!.toByteArray()

        if(layoutInflater ==null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if(convertView == null){
            convertView = layoutInflater!!.inflate(R.layout.reading_list_entry, null)
        }

        // Composition of every row
        comicName = convertView!!.findViewById(R.id.manga_name)
        chapterCounter = convertView.findViewById(R.id.chapter)
        circleImage = convertView.findViewById(R.id.image_circle)

        comicName.text = comic.title

        chapterCounter.text = comic.id.toString()

        // Still a work in progress
        // not sure if you can convert a binary file to base64
        circleImage.setImageBitmap(
            BitmapFactory.decodeByteArray(comicImageBase64, 0, comicImageBase64.size)
        )

        return convertView
    }
}