package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import it.unitn.disi.lpsmt.g03.mangacheck.R

internal class ReadingByNameAdapter(private val comicName :String, private val context: Context): BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null

    private lateinit var comicNameToDisplay : TextView

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if(layoutInflater ==null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if(convertView == null){
            convertView = layoutInflater!!.inflate(R.layout.entry_select_by_name, null)
        }

        comicNameToDisplay = convertView!!.findViewById(R.id.container_manga_name)
        comicNameToDisplay.text = comicName

        Log.v("Comic name", comicName)

        return convertView
    }
}