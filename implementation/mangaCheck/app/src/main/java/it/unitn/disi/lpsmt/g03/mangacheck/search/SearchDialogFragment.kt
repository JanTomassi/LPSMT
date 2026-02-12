package it.unitn.disi.lpsmt.g03.mangacheck.search

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import it.unitn.disi.lpsmt.g03.mangacheck.R

class SearchDialogFragment(private val currentPage: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = layoutInflater.inflate(R.layout.search_dialog, null)
            val textView = (view.findViewById(R.id.chapter_number) as TextView)
            textView.text = currentPage.toString()
            textView.setOnEditorActionListener { lTextView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setResult(lTextView)
                    requireDialog().dismiss()
                    true
                } else false
            }
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setResult(textView: TextView) {
        setFragmentResult(tag ?: SearchDialogFragment::class.simpleName!!, Bundle().apply {
            try {
                putInt("page", textView.text.toString().toInt())
            } catch (e: NumberFormatException) {
                Log.v(TAG, "textView text is NaN: ${textView.text}")
                putInt("page", currentPage)
            }
        })
    }

    companion object {
        val TAG = SearchDialogFragment::class.simpleName
    }
}