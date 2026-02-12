package it.unitn.disi.lpsmt.g03.mangacheck.list_comic.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.ListComicFragmentDirections
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import java.io.File

class ListComicAdapter(
    private val chaptersList: MutableList<ChapterEntry>,
    originalFragment: Fragment,
    private val navController: NavController,
    private val libraryID: Int
) : BaseAdapter() {

    private val context by lazy { originalFragment.requireContext() }
    private val layoutInflater by lazy {
        (context.getSystemService() as LayoutInflater?) ?: throw IllegalStateException("Can't get a layout Inflater")
    }

    override fun getCount(): Int {
        return chaptersList.size
    }

    override fun getItem(position: Int): Any {
        return chaptersList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: layoutInflater.inflate(R.layout.list_comic_entry, null)

        val mangaName: TextView = view.findViewById(R.id.manga_name)
        val mangaChapter: TextView = view.findViewById(R.id.letter_circle)

        mangaName.text = chaptersList[position].title
        mangaChapter.text = chaptersList[position].num.toString()

        view.setOnClickListener {
            val direction = ListComicFragmentDirections.actionListComicFragmentToReaderFragment(
                File(
                    context.filesDir, "/$libraryID/${chaptersList[position].num}.cbz"
                ).path
            )
            navController.navigate(direction)
        }

        return view
    }
}