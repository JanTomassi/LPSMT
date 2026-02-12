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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_library.AddLibraryFragment
import it.unitn.disi.lpsmt.g03.mangacheck.add_library.AddLibraryFragmentDirections
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException

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
                if (!XMLParser(context).mangaAlreadyInList(xmlFile, libraryName, "library")) {
                    queryImage(libraryId)
                } else {
                    toaster("Library already in list")
                }
            }
        }
        return convertView
    }

    // Request the cover image to teh server and then trigger the navigation event
    private fun queryImage(libraryId: Int) {
        val client = HttpClient()
        val scope = CoroutineScope(Dispatchers.IO)
        val context = originatingFragment.requireContext()
        val ipAddr: String = context.getString(R.string.ip_addr)
        val serverPort: Int = context.getString(R.string.server_port).toInt()

        scope.launch {
            try {
                val response: HttpResponse = client.get {
                    url {
                        host = ipAddr
                        port = serverPort
                        path("image/${libraryId}")
                    }
                }
                if (response.status.value in 200..299) {
                    withContext(Dispatchers.IO){
                        val imageString = response.body<ByteArray>()
                        val out = FileOutputStream(File(context.cacheDir, libraryId.toString()))
                        out.write(imageString)
                        out.flush()
                        out.close()
                    }
                    val action: NavDirections =
                        AddLibraryFragmentDirections.actionAddLibraryFragmentToLibraryFragment(libraryId, libraryName)
                    withContext(Dispatchers.Main) {
                        this@AddLibraryAdapter.originatingFragment.findNavController()
                            .navigate(action)
                    }
                }
            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        val context = originatingFragment.requireContext()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}