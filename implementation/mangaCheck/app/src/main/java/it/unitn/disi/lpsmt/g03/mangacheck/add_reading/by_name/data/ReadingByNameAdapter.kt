package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.AddReadingByNameFragment
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.AddReadingByNameFragmentDirections
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameEntryBinding

internal class ReadingByNameAdapter(private val comicName: String, private val context: Context) :
    BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null

    private lateinit var comicNameToDisplay: Button

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    // In this case the position is the manga ID, the id is negative only if the button is the
    // error message so no event on click set
    override fun getView(mangaID: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.add_reading_select_by_name_entry, null)
        }

        comicNameToDisplay = convertView!!.findViewById(R.id.container_manga_name)
        comicNameToDisplay.text = comicName

        if (mangaID > -1) {
            comicNameToDisplay.setOnClickListener {
                val action : NavDirections =
                    AddReadingByNameFragmentDirections.actionAddReadingByNameToAddReadingSetStatus(
                        mangaID,
                        comicName
                    )
                it.findNavController().navigate(action)
            }
        }

        return convertView
    }
}