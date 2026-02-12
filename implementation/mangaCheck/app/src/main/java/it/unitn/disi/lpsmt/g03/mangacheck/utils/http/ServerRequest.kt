package it.unitn.disi.lpsmt.g03.mangacheck.utils.http

import android.content.Context
import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException

class ServerRequest(private val context : Context, private val mangaID : Int) {

    private val client = HttpClient()
    private val ipAddr: String = context.getString(R.string.ip_addr)
    private val serverPort: Int = context.getString(R.string.server_port).toInt()

    // Make a request to the server for the Manga cover image
    fun queryImage() {
        if (!File(context.cacheDir, mangaID.toString()).exists()) {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                try {
                    val response: HttpResponse = client.get {
                        url {
                            host = ipAddr
                            port = serverPort
                            path("image/${mangaID}")
                        }
                    }
                    if (response.status.value in 200..299) {
                        withContext(Dispatchers.IO){
                            val imageString = response.body<ByteArray>()
                            val out = FileOutputStream(File(context.cacheDir, mangaID.toString()))
                            out.write(imageString)
                            out.flush()
                            out.close()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            toaster("Error ${response.status.value}")
                        }
                    }
                } catch (e: ConnectException) {
                    withContext(Dispatchers.Main) {
                        toaster("Connection Refused")
                    }
                }
            }
        }
    }

    fun queryDescription() : String {
        val scope = CoroutineScope(Dispatchers.IO)
        var responseToReturn : String = String()

        scope.launch {
            try {
                val response: HttpResponse = client.get {
                    url {
                        host = ipAddr
                        port = serverPort
                        path("description/${mangaID}")
                    }
                }
                if (response.status.value in 200..299) {
                    responseToReturn = response.body()
                }
            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
        return responseToReturn
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}