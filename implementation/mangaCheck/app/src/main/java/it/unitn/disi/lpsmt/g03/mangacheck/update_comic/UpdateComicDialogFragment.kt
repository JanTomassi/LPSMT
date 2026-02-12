package it.unitn.disi.lpsmt.g03.mangacheck.update_comic

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.UpdateComicsDialogBinding
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class UpdateComicDialogFragment(
    private val chapterEntry: ChapterEntry,
    private val xmlEncoder: XMLEncoder,
    private val xmlParser: XMLParser,
    private val cbzFile: File
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = UpdateComicsDialogBinding.inflate(layoutInflater)

            view.dismissDialog.setOnClickListener {
                requireDialog().dismiss()
            }

            // Title of the chapter
            val chapterTitle = view.mangaTitle
            chapterTitle.setText(chapterEntry.title)

            val chapterNum = view.chapterInput.input
            chapterNum.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateEntry(
                        chapterEntry,
                        ChapterEntry(
                            chapterNum.text.toString().toInt(),
                            chapterTitle.text.toString()
                        )
                    )
                    requireDialog().dismiss()
                    true
                } else false
            }

            val addButton = view.submitButton
            addButton.setOnClickListener {
                updateEntry(
                    chapterEntry,
                    ChapterEntry(
                        chapterNum.text.toString().toInt(),
                        chapterTitle.text.toString()
                    )
                )
            }

            val deleteButton = view.deleteButton
            deleteButton.setOnClickListener { }

            builder.setView(view.root)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updateEntry(oldEntry: ChapterEntry, newEntry: ChapterEntry) {
        if (xmlParser.alreadyInList(newEntry)) {
            setFragmentResult(TAG!!, Bundle().apply {
                putString("title", oldEntry.title)
                putInt("num", oldEntry.num)
            });return
        }
        
        xmlEncoder.modifyEntry(oldEntry, "title", newEntry.title)
        xmlEncoder.modifyEntry(oldEntry, "num", newEntry.num.toString())

        val oldPath = Paths.get(cbzFile.toString() + "/${oldEntry.num}.cbz")
        val newPath = Paths.get(cbzFile.toString() + "/${newEntry.num}.cbz")
        Files.move(oldPath, newPath)

        setResult(newEntry)
    }

    private fun setResult(entry: ChapterEntry) {
        setFragmentResult(TAG!!, Bundle().apply {
            putString("title", entry.title)
            putInt("num", entry.num)
        })
    }

    companion object {
        val TAG = UpdateComicDialogFragment::class.simpleName
    }

}