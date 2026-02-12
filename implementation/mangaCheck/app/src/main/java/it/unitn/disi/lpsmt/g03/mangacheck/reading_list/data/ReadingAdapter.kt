package it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import it.unitn.disi.lpsmt.g03.mangacheck.R

internal class ReadingAdapter(
    private val comicList: List<ReadingModal>, private val context: Context
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var comicName: TextView
    private lateinit var chapterCounter: TextView
    private lateinit var circleLetter: TextView

    override fun getCount(): Int {
        return comicList.size
    }

    override fun getItem(position: Int): Any {
        return comicList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if(layoutInflater ==null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if(convertView == null){
            convertView = layoutInflater!!.inflate(R.layout.reading_list_entry, null)
        }

        // Composition of every row
        comicName = convertView!!.findViewById(R.id.manga_name)
        chapterCounter = convertView!!.findViewById(R.id.chapter)
        circleLetter = convertView!!.findViewById(R.id.letter_circle)

        comicName.text = comicList[position].seriesName

        // Composition of the chapter counter
        val chapterString : String = "Ch. "+comicList[position].seriesCurrentChapters+"/"+comicList[position].seriesTotalChapters
        chapterCounter.text = chapterString
        chapterCounter.setTextColor(R.color.VNatural_30)/*theme.colorOnSurfaceVariant*/

        //Instead of this the manga cover should fit as well
        circleLetter.text = comicList[position].seriesName.get(0).toString()

        return convertView
    }
}