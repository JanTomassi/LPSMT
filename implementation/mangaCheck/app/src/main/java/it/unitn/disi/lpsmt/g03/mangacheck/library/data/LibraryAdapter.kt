package it.unitn.disi.lpsmt.g03.mangacheck.library.data

import android.content.Context
import android.graphics.BitmapFactory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class LibraryAdapter(
    private val seriesList: List<LibraryEntry>, private val context: Context
) : BaseAdapter() {
    // in base adapter class we are creating variables
    // for layout inflater, course image view and course text view.
    private var layoutInflater: LayoutInflater? = null
    private lateinit var seriesTextView: TextView
    private lateinit var seriesImageView: ImageView

    override fun getCount(): Int {
        return seriesList.size
    }

    override fun getItem(position: Int): Any {
        return seriesList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    // Generate the view for the library button with the cover image
    @OptIn(ExperimentalEncodingApi::class)
    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.library_card, null)
        }

        seriesImageView = convertView!!.findViewById(R.id.image)
        seriesTextView = convertView.findViewById(R.id.text)

        val libraryImageBase64 = Base64.decode(seriesList[pos].image!!.toByteArray(Charsets.UTF_8))
        seriesImageView.setImageBitmap(
            BitmapFactory.decodeByteArray(libraryImageBase64, 0, libraryImageBase64.size)
        )

        seriesTextView.text = seriesList[pos].title

        return convertView
    }


}