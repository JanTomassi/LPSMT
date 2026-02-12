package it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data

import android.app.AlertDialog.Builder
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.Entry
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class ReadingAdapter(
    private val comicsList: List<Entry>, private val context: Context
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

    // Make the dialog pawn and set the border transparencies
    private fun dialogSpawner(comic : Entry) : Boolean{
        val dialogView : View = layoutInflater!!.inflate(R.layout.info_reading_dialog, null)
        val dialogTitle : TextView = dialogView.findViewById(R.id.manga_title)
        val dialogDescription : TextView = dialogView.findViewById(R.id.manga_description)

        dialogTitle.text = comic.title
        dialogDescription.text = comic.description

        val dialogBuilder = Builder(context)
            .setView(dialogView)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val closeButton : Button = dialogView.findViewById(R.id.dismiss_dialog)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        return true // Return true to consume the event
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        val comic: Entry = getItem(position)
        val comicImageBase64 = Base64.decode( comic.image!!)

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.reading_list_entry, null)
        }

        // Composition of every row
        comicName = convertView!!.findViewById(R.id.manga_name)
        chapterCounter = convertView.findViewById(R.id.chapter)
        circleImage = convertView.findViewById(R.id.image_circle)

        comicName.text = comic.title

        //Temporary
        chapterCounter.text = comic.id.toString()

        circleImage.setImageBitmap(
            BitmapFactory.decodeByteArray(comicImageBase64, 0, comicImageBase64.size)
        )

        convertView.setOnLongClickListener {
            dialogSpawner(comic)
        }

        return convertView
    }

}