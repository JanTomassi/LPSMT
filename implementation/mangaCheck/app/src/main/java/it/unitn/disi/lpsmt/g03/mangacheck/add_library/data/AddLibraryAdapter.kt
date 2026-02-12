package it.unitn.disi.lpsmt.g03.mangacheck.add_library.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameEntryBinding
import it.unitn.disi.lpsmt.g03.mangacheck.library.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.library.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.http.ServerRequest
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddLibraryAdapter(
    private val response: Array<Array<String>>, private val context: Context, private val navController: NavController
) : BaseAdapter() {

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
    override fun getView(position: Int, oldView: View?, parent: ViewGroup?): View {
        val view = oldView ?: AddReadingSelectByNameEntryBinding.inflate(layoutInflater, parent, false).root

        val button: Button = view.findViewById(R.id.container_manga_name)

        button.text = response[position][1]
        if (response[position][0].toInt() > -1) {
            view.setOnClickListener {
                if (!XMLParser(context).alreadyInList(
                        LibraryEntry(
                            response[position][1], response[position][0].toInt()
                        )
                    )
                ) {
                    XMLEncoder(context).addEntry(LibraryEntry(response[position][1], response[position][0].toInt()))
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.IO) {
                            ServerRequest(
                                context, response[position][0].toInt()
                            ).queryImage()
                        }
                        withContext(Dispatchers.Main) { navController.popBackStack() }
                    }
                }
            }
        }
        return view
    }
}