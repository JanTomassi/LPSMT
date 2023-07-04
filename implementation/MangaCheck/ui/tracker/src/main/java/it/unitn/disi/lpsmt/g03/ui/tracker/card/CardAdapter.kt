package it.unitn.disi.lpsmt.g03.ui.tracker.card

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.R
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.ModifyDialogBinding
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardAdapter(
        private val dataSet: List<TrackerSeries>, private val glide: RequestManager
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private lateinit var parentGlob: ViewGroup

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ViewHolder(view: TrackerCardBinding) :
            RecyclerView.ViewHolder(view.root) {
        var seriesCover: ImageView = view.seriesCover
        var seriesTitle: TextView = view.seriesTitle
        var chCounter: TextView = view.chCounter
        var modifyButton: Button = view.modifyButton
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TrackerCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        parentGlob = parent
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Set the image in the card
        val requestOptions = RequestOptions().transform(
                FitCenter(), RoundedCorners(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
                ).toInt()
        )
        )

        glide.load(dataSet[position].imageUri).error(glide.load(R.drawable.baseline_broken_image_24))
                .apply(requestOptions).into(viewHolder.seriesCover)

        // Set the Series title in the card
        viewHolder.seriesTitle.text = dataSet[position].title

        // Set the Series chapter counter in the card
        if (dataSet[position].chapters != null)
            viewHolder.chCounter.text = dataSet[position].chapters.toString()
        else
            viewHolder.chCounter.text = null

        viewHolder.modifyButton.setOnClickListener {
            dialogSpawner(dataSet[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size
    }

    private fun dialogSpawner(series: TrackerSeries){
        val dialogView: ModifyDialogBinding = ModifyDialogBinding.inflate(LayoutInflater.from(parentGlob.context), parentGlob, false)
        val closeButton: Button = dialogView.dismissDialog
        val dialogTitle: TextView = dialogView.mangaTitle
        val dialogDescription: TextView = dialogView.mangaDescription
        val statusSpinner: Spinner = dialogView.listSelector
        val submitButton: Button = dialogView.submitButtonDialog

        dialogTitle.text = series.title
        dialogDescription.text = series.description

        statusSpinner.adapter = ArrayAdapter<ReadingState>(
                parentGlob.context,
                R.layout.dropdown_menu_popup_item,
                ReadingState.values()
        )

        submitButton.setOnClickListener {
            val newStatus = ReadingState.valueOf(statusSpinner.adapter.toString())

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(parentGlob.context).trackerSeriesDao()
                        .updateStatus(series.uid, newStatus)
            }
        }

        val dialogBuilder =
                AlertDialog.Builder(parentGlob.context).setView(dialogView.root)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}