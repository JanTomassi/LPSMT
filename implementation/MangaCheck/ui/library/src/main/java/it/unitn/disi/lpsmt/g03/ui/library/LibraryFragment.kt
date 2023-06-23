package it.unitn.disi.lpsmt.g03.ui.library

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.library.Series
import it.unitn.disi.lpsmt.g03.mangacheck.MainActivity
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryCardBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment(), ActionMode.Callback {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db: AppDatabase.AppDatabaseInstance by lazy { AppDatabase.getInstance(requireContext()) }
    private var actionMode: ActionMode? = null
    private lateinit var tracker: SelectionTracker<Long>

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
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        CoroutineScope(Dispatchers.IO).launch {
            val dataSet: List<Series> = AppDatabase.getInstance(context).seriesDao().getAllByLastAccess()
            val adapter = LibraryAdapter(dataSet, Glide.with(this@LibraryFragment))
            val layoutManager = GridLayoutManager(context, 2)
            val decoration = LibraryRecyclerViewDecoration(2, 16, true)

            withContext(Dispatchers.Main) {
                seriesGRV.apply {
                    this.adapter = adapter
                    this.layoutManager = layoutManager
                    this.addItemDecoration(decoration)
                }
                tracker = SelectionTracker.Builder(
                    "selectionItemForLibrary",
                    binding.libraryView,
                    LibraryAdapter.ItemsKeyProvider(adapter),
                    LibraryAdapter.ItemsDetailsLookup(binding.libraryView),
                    StorageStrategy.createLongStorage()
                ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
                tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()

                        if (actionMode == null) {
                            val currentActivity = activity as MainActivity
                            actionMode = currentActivity.startSupportActionMode(this@LibraryFragment)
                        }
                        val items = tracker.selection.size()
                        if (items > 0) {
                            actionMode?.title = "$items selected"
                        } else {
                            actionMode?.finish()
                        }
                    }
                })
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                val libraryAdapter = binding.libraryView.adapter as LibraryAdapter

                val selected = libraryAdapter.dataSet.filter {
                    tracker.selection.contains(it.uid)
                }.toMutableList()

                val newDataSet = libraryAdapter.dataSet.toMutableList()

                newDataSet.removeAll(selected)

                libraryAdapter.update(newDataSet)
                actionMode?.finish()
                true
            }

            else -> {
                false
            }
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker.clearSelection()
        actionMode = null

        val adapter = (binding.libraryView.adapter as LibraryAdapter)
        binding.libraryView.adapter = adapter
    }

    inner class LibraryRecyclerViewDecoration(
        private val spanCount: Int, space: Int, private val includeEdge: Boolean
    ) : ItemDecoration() {
        private val dp: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, space.toFloat(), Resources.getSystem().displayMetrics
        ).toInt()

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = dp - column * dp / spanCount
                outRect.right = (column + 1) * dp / spanCount
                if (position < spanCount) {
                    outRect.top = dp
                }
                outRect.bottom = dp
            } else {
                outRect.left = column * dp / spanCount
                outRect.right = dp - (column + 1) * dp / spanCount
                if (position >= spanCount) {
                    outRect.top = dp
                }
            }
        }
    }

    class LibraryAdapter(
        var dataSet: List<Series>, private val glide: RequestManager
    ) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LibraryCardBinding.inflate(LayoutInflater.from(parent.context))
            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val view = viewHolder.view

            val requestOptions = RequestOptions().transform(
                FitCenter(), RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
                    ).toInt()
                )
            )

            view.text.text = dataSet[position].title
            glide.load(dataSet[position].imageUri).error(glide.load(R.drawable.baseline_broken_image_24))
                .apply(requestOptions).into(view.image)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

        fun update(list: List<Series>) {
            dataSet = list
            notifyItemRangeChanged(0, list.size)
        }

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        inner class ViewHolder(val view: LibraryCardBinding) : RecyclerView.ViewHolder(view.root) {
            fun getItem(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long = dataSet[adapterPosition].uid
            }
        }

        class ItemsDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                val view = recyclerView.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    return (recyclerView.getChildViewHolder(view) as ViewHolder).getItem()
                }
                return null
            }
        }

        class ItemsKeyProvider(private val adapter: LibraryAdapter) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
            override fun getKey(position: Int): Long = adapter.dataSet[position].uid
            override fun getPosition(key: Long): Int = adapter.dataSet.indexOfFirst { it.uid == key }
        }

    }

}