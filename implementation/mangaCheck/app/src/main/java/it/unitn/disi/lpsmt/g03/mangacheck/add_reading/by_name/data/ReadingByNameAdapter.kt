package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.AddReadingByNameFragmentDirections
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameEntryBinding
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.MangaEntry

internal class ReadingByNameAdapter(
    private val response: Array<Array<String>>,
    private val context: Context,
    private val navController: NavController
) :
    BaseAdapter() {

    private val layoutInflater by lazy {
        (context.getSystemService() as LayoutInflater?) ?: throw IllegalStateException("Can't get a layout Inflater")
    }

    override fun getCount(): Int {
        return response.size
    }

    override fun getItem(position: Int): Any {
        return response[position][1]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // In this case the position is the manga ID, the id is negative only if the button is the
    // error message so no event on click set
    override fun getView(pos: Int, oldView: View?, parent: ViewGroup?): View {
        val view = oldView ?: AddReadingSelectByNameEntryBinding.inflate(layoutInflater, parent, false).root

        val button: Button = view.findViewById(R.id.container_manga_name)

        button.text = response[pos][1]
        if (response[pos][0].toInt() > -1) {
            view.setOnClickListener {
                XMLParser(context).alreadyInList(
                    MangaEntry("", response[pos][1], response[pos][0].toInt(), null)
                )
                if (!XMLParser(context).alreadyInList(
                        MangaEntry("", response[pos][1], response[pos][0].toInt(), null)
                    )
                ) {
                    button.text = context.getString(R.string.add_comic_fetching)
                    val direction = AddReadingByNameFragmentDirections.actionAddReadingByNameToAddReadingSetStatus(
                        response[pos][0].toInt(),
                        response[pos][1]
                    )
                    navController.navigate(direction)
                }
            }
        }
        return view
    }
}