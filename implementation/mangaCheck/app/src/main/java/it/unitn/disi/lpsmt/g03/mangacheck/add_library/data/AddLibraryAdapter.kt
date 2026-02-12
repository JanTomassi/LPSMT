package it.unitn.disi.lpsmt.g03.mangacheck.add_library.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.ConnectException

class AddLibraryAdapter(
    private val libraryName: String, private val context: Context
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

    override fun getView(libraryId: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
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
                if(!XMLParser().mangaAlreadyInList(xmlFile,libraryName, "library")){
                    queryImage(libraryId)
                }
                else{
                    toaster("Library already in list")
                }
            }
        }
        return convertView
    }

    private fun queryImage(libraryId : Int) {
        val client = HttpClient()
        val scope = CoroutineScope(Dispatchers.Main)
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
                checkQueryResult(response)
            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
    }

    private suspend fun checkQueryResult(response: HttpResponse) {
        if (response.status.value in 200..299) {
            val imageBase64 :String = response.body()
        } else {
            withContext(Dispatchers.Main) {
                toaster("Error ${response.status.value}")
            }
        }
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}