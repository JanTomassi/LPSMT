package it.unitn.disi.lpsmt.g03.ui.library

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.library.Chapter
import it.unitn.disi.lpsmt.g03.library.Series
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryCardBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db: AppDatabase.AppDatabaseInstance by lazy { AppDatabase.getInstance(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LibraryLayoutBinding.inflate(inflater, container, false)
        // initializing variables of grid view with their ids.
        seriesGRV = binding.libraryView

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_libraryFragment_to_series_series)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val chapters: List<Chapter>
            withContext(Dispatchers.IO) {
                chapters = db.chapterDao().getAll()
            }
            withContext(Dispatchers.Main) {
                populateLibrary(chapters)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Empty the grid view and parse the xml to repopulate the view
    private fun populateLibrary(chapters: List<Chapter>) {

        CoroutineScope(Dispatchers.IO).launch {
            val dataSet: List<Series> = AppDatabase.getInstance(context).seriesDao().getAllByLastAccess()

            withContext(Dispatchers.Main) {
                seriesGRV.adapter = LibraryAdapter(dataSet, Glide.with(this@LibraryFragment))
                seriesGRV.layoutManager = GridLayoutManager(context, 2)
                seriesGRV.addItemDecoration(LibraryRecyclerViewDecoration(2, 16, true))
            }
        }
    }

    class LibraryRecyclerViewDecoration(
        private val spanCount: Int, private val space: Int, private val includeEdge: Boolean
    ) : ItemDecoration() {
        private val dp: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, space.toFloat(), Resources.getSystem().displayMetrics
        ).toInt()

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column
            if (includeEdge) {
                outRect.left = dp - column * dp / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * dp / spanCount // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = dp
                }
                outRect.bottom = dp // item bottom
            } else {
                outRect.left = column * dp / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    dp - (column + 1) * dp / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = dp // item top
                }
            }
        }
    }

    class LibraryAdapter(private val dataSet: List<Series>, private val glide: RequestManager) :
        RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        data class ViewHolder(val view: LibraryCardBinding) : RecyclerView.ViewHolder(view.root)

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LibraryCardBinding.inflate(LayoutInflater.from(parent.context))
            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val view = viewHolder.view

            view.text.text = dataSet[position].title
            val requestOptions = RequestOptions().transform(
                FitCenter(), RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
                    ).toInt()
                )
            )
            glide.load(dataSet[position].imageUrl).apply(requestOptions).into(view.image)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }

}