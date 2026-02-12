package it.unitn.disi.lpsmt.g03.ui.tracker.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeriesDao
import it.unitn.disi.lpsmt.g03.ui.tracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyDialog(ctx: Context, private val item: TrackerSeries) : DialogFragment() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ModifyDialogEntryPoint {
        fun provideTrackerSeriesDao(): TrackerSeriesDao
    }

    var trackerSeriesDao: TrackerSeriesDao

    init {
        val myLibraryAdapterEntryPoint = EntryPointAccessors.fromApplication(ctx,
            ModifyDialogEntryPoint::class.java)
        trackerSeriesDao = myLibraryAdapterEntryPoint.provideTrackerSeriesDao()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.modify_dialog, null)
        view.findViewById<TextView>(R.id.manga_description).text = removeLastNChars(item.description)
        view.findViewById<Spinner>(R.id.list_selector).adapter = ArrayAdapter(requireContext(),
            R.layout.dropdown_menu_popup_item,
            ReadingState.values())

        // Create and configure the AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(item.title)
            .setView(view)
            .setPositiveButton(R.string.modify) { dialog, _ ->
                modifyStatus(view.findViewById(R.id.list_selector))
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Create the dialog and return it
        val dialog = alertDialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_surface)
        return dialog
    }

    private fun modifyStatus(statusSpinner: Spinner) {
        val newStatus = ReadingState.valueOf(statusSpinner.selectedItem.toString())

        CoroutineScope(Dispatchers.IO).launch {
            trackerSeriesDao.updateStatus(item.uid, newStatus)
        }
    }

    /**
     * Prevent the creation of too long dialogs
     */
    private fun removeLastNChars(input: String?): String? {
        return if (input != null && input.length >= 500) {
            input.substring(0, 400)
        } else {
            input
        }
    }
}
