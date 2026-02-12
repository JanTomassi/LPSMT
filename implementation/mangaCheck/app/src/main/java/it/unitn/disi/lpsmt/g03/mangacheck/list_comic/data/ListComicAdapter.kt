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
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.update_comic.UpdateComicDialogFragment
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import java.io.File

class ListComicAdapter(
    private val xmlParser: XMLParser,
    private val xmlEncoder: XMLEncoder,
    private val originalFragment: Fragment,
    private val navController: NavController,
    private val libraryID: Int
) : BaseAdapter() {

    private val context by lazy { originalFragment.requireContext() }
    private val layoutInflater by lazy {
        (context.getSystemService() as LayoutInflater?) ?: throw IllegalStateException("Can't get a layout Inflater")
    }
    private val chaptersList: MutableList<ChapterEntry>
        get() {
            return xmlParser.parse()
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

        // Get a local ref to pass only one entry to lambda closure
        val chapter = chaptersList[position]

        mangaName.text = chapter.title
        mangaChapter.text = chapter.num.toString()

        view.setOnClickListener {
            val direction = ListComicFragmentDirections.actionListComicFragmentToReaderFragment(
                File(
                    context.filesDir.toString() + "/$libraryID/",
                    "${chapter.num}.cbz"
                ).path
            )
            navController.navigate(direction)
        }

        view.setOnLongClickListener {
            val dialog = UpdateComicDialogFragment(
                chaptersList[position],
                xmlEncoder,
                xmlParser,
                File(context.filesDir.toString() + "/$libraryID/")
            )
            dialog.show(originalFragment.childFragmentManager, UpdateComicDialogFragment::class.simpleName)
            originalFragment.childFragmentManager.setFragmentResultListener(
                UpdateComicDialogFragment::class.simpleName!!,
                originalFragment.viewLifecycleOwner
            ) { requestKey, bundle ->
                if (requestKey != UpdateComicDialogFragment::class.simpleName)
                    return@setFragmentResultListener
                val newTitle = bundle.getString("title")
                val newNum = bundle.getInt("num")
                if (newTitle == null || newNum == 0)
                    return@setFragmentResultListener
                mangaName.text = newTitle
                mangaChapter.text = newNum.toString()
            }
            true
        }

        return view
    }
}