package it.unitn.disi.lpsmt.g03.mangacheck.library.data

import android.app.AlertDialog.Builder
import android.content.Context
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
import it.unitn.disi.lpsmt.g03.mangacheck.library.LibraryFragment
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry

internal class LibraryAdapter(
    private val seriesList: List<LibraryEntry>, private val originatingFragment: LibraryFragment
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
    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if (layoutInflater == null) {
            layoutInflater =
                originatingFragment.requireContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.library_card, null)
        }

        seriesImageView = convertView!!.findViewById(R.id.image)
        seriesTextView = convertView.findViewById(R.id.text)

//        seriesImageView.setImageBitmap(
//            seriesList[pos].image
//        )

        seriesTextView.text = seriesList[pos].title

        convertView.setOnLongClickListener {
            dialogSpawner(seriesList[pos])
        }

        return convertView
    }

    // Make the dialog spawn and set the border transparencies and actions
    private fun dialogSpawner(library: LibraryEntry): Boolean {
        val dialogView: View = layoutInflater!!.inflate(R.layout.remove_library_dialog, null)
        val closeButton: Button = dialogView.findViewById(R.id.dismiss_dialog)
        val dialogWarning: TextView = dialogView.findViewById(R.id.warning_message)
        val submitButton: Button = dialogView.findViewById(R.id.submit_button_dialog)

        dialogWarning.append(" " + library.title + "?")

        val dialogBuilder = Builder(originatingFragment.requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Send the new list to the ReadingListFragment, attempt to refresh
        submitButton.setOnClickListener {
            dialog.dismiss()
            originatingFragment.onDataReceived(library)
        }

        dialog.show()

        return true
    }

}