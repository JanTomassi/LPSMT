package it.unitn.disi.lpsmt.g03.ui.tracker.category

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.R
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding
import it.unitn.disi.lpsmt.g03.ui.tracker.dialog.ModifyDialog
import java.lang.Integer.max

class CategoryAdapter(
    private val ctx: Context,
    val name: ReadingState,
    private val glide: RequestManager,
    private val manager: FragmentManager,
    private val lifeCycle: LifecycleOwner
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    lateinit var view: View
    private var dataSet: List<TrackerSeries> = emptyList()
    private var liveDataSet: LiveData<List<TrackerSeries>> =
        AppDatabase.getInstance(ctx).trackerSeriesDao().getAllByStatus(name)

    private val requestOptions = RequestOptions().transform(
        FitCenter(), RoundedCorners(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
            ).toInt()
        )
    )

    init {

        liveDataSet.observe(lifeCycle) { newData ->
            if (newData.isEmpty()) view.visibility = View.GONE
            else view.visibility = View.VISIBLE
            val len = max(dataSet.size, newData.size)
            dataSet = newData
            notifyItemRangeChanged(0, len)
        }

    }

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ViewHolder(view: TrackerCardBinding) :
        RecyclerView.ViewHolder(view.root) {
        private var seriesCover: ImageView = view.seriesCover
        private var seriesTitle: TextView = view.seriesTitle
        private var chCounter: TextView = view.chCounter
        private var modifyButton: Button = view.modifyButton

        fun bind(item: TrackerSeries) {
            glide.load(item.imageUri)
                .error(glide.load(R.drawable.baseline_broken_image_24))
                .apply(requestOptions).into(seriesCover)

            // Set the Series title in the card
            seriesTitle.text = item.title

            // Set the Series chapter counter in the card
            if (item.chapters != null)
                chCounter.text = item.chapters.toString()
            else
                chCounter.text = null

            modifyButton.setOnClickListener {
                val dialogFragment = ModifyDialog(item)
                dialogFragment.show(manager, "CustomDialog")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TrackerCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}