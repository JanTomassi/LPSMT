package it.unitn.disi.lpsmt.g03.mangacheck.add_library.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_library.AddLibraryFragment
import it.unitn.disi.lpsmt.g03.mangacheck.add_library.AddLibraryFragmentDirections
import it.unitn.disi.lpsmt.g03.mangacheck.library.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.http.ServerRequest
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddLibraryAdapter(
    private val libraryName: String, private val originatingFragment: AddLibraryFragment
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null

    private lateinit var libraryNameToDisplay: Button

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    // Generate the button with the library name and set onclick action
    override fun getView(libraryId: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        val context = originatingFragment.requireContext()

        val xmlFile = File(context.filesDir, context.getString(R.string.library_XML))

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.add_reading_select_by_name_entry, null)
        }

        libraryNameToDisplay = convertView!!.findViewById(R.id.container_manga_name)
        libraryNameToDisplay.text = libraryName

        if (libraryId > -1) {
            libraryNameToDisplay.setOnClickListener {
                if (!XMLParser().alreadyInList(xmlFile, LibraryEntry(libraryName, libraryId))) {
                    libraryNameToDisplay.text =
                        originatingFragment.requireContext().getString(R.string.add_comic_fetching)
                    CoroutineScope(Dispatchers.IO).launch {
                        ServerRequest(context, libraryId).queryImage()
                        withContext(Dispatchers.Main) {
                            val action: NavDirections =
                                AddLibraryFragmentDirections.actionAddLibraryFragmentToLibraryFragment(
                                    libraryId, libraryName
                                )
                            originatingFragment.findNavController().navigate(action)
                        }
                    }
                } else {
                    toaster("Library already in list")
                }
            }
        }
        return convertView
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        val context = originatingFragment.requireContext()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}