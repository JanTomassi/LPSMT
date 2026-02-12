package it.unitn.disi.lpsmt.g03.mangacheck.library.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import it.unitn.disi.lpsmt.g03.mangacheck.R

internal class LibraryAdapter(
    private val seriesList: List<LibraryModal>, private val context: Context
) : BaseAdapter() {
    // in base adapter class we are creating variables
    // for layout inflater, course image view and course text view.
    private var layoutInflater: LayoutInflater? = null
    private lateinit var courseTV: TextView
    private lateinit var courseIV: ImageView

    override fun getCount(): Int {
        return seriesList.size
    }

    override fun getItem(position: Int): Any {
        return seriesList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if(layoutInflater ==null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if(convertView == null){
            convertView = layoutInflater!!.inflate(R.layout.library_card, null)
        }

        courseIV = convertView!!.findViewById(R.id.image)
        courseTV = convertView!!.findViewById(R.id.text)

        courseIV.setImageResource(seriesList[pos].seriesImage)
        courseTV.text = seriesList[pos].seriesName

        return convertView
    }


}