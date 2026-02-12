package it.unitn.disi.lpsmt.g03.mangacheck.info_reading

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.InfoReadingDialogBinding

class InfoReadingDialogFragment(context: Context, comic: String) : AlertDialog(context) {
    private var _binding: InfoReadingDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dialogView: View =
            LayoutInflater.from(ownerActivity).inflate(R.layout.info_reading_dialog, null)
        val dialogBuilder = AlertDialog.Builder(ownerActivity).setView(dialogView)


        setContentView(R.layout.info_reading_dialog)
    }

}