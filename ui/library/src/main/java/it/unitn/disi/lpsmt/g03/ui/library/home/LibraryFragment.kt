package it.unitn.disi.lpsmt.g03.ui.library.home

import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.ui.library.common.CustomAdapter
import it.unitn.disi.lpsmt.g03.ui.library.LibraryFragmentDirections
import it.unitn.disi.lpsmt.g03.ui.library.R
import it.unitn.disi.lpsmt.g03.ui.library.common.RecyclerViewGridDecoration
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryCardBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import kotlin.math.max


class LibraryFragment : Fragment() {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db: AppDatabase.AppDatabaseInstance by lazy {
        AppDatabase.getInstance(requireContext())
    }
    private var actionMode: ActionMode? = null
    private lateinit var tracker: SelectionTracker<Long>
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = LibraryLayoutBinding.inflate(inflater,
            container,
            false) // initializing variables of grid view with their ids.
        seriesGRV = binding.libraryView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
        tracker.clearSelection()
        _binding = null
    }

    @MainThread
    private fun initUI() {
        val decoration = RecyclerViewGridDecoration(2, 16, true)
        val layoutManager = GridLayoutManager(context, 2)
        val adapter = LibraryAdapter(emptyList(), Glide.with(this@LibraryFragment), navController)
        seriesGRV.apply {
            this.addItemDecoration(decoration)
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dataSet: List<Series> = AppDatabase.getInstance(context)
                .seriesDao()
                .getAllSortByLastAccess()
            withContext(Dispatchers.Main) {
                (seriesGRV.adapter as LibraryAdapter).update(dataSet)
            }
        }

        tracker = SelectionTracker.Builder("selectionItemForLibrary",
            binding.libraryView,
            LibraryAdapter.ItemsKeyProvider(adapter),
            LibraryAdapter.ItemsDetailsLookup(binding.libraryView),
            StorageStrategy.createLongStorage())
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()

                if (actionMode == null) {
                    val currentActivity = activity as AppCompatActivity
                    actionMode = currentActivity.startSupportActionMode(SelectionCallback())
                }
                val items = tracker.selection.size()
                if (items > 0) {
                    actionMode?.title = "$items selected"
                } else {
                    actionMode?.finish()
                }
            }
        })
        adapter.tracker = tracker
        binding.addButton.setOnClickListener {
            navController.navigate(R.id.action_library_to_series_series)
        }
    }

    private inner class SelectionCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    val libraryAdapter = binding.libraryView.adapter as LibraryAdapter
                    val selected: List<Series> = (libraryAdapter.dataSet.filter {
                        tracker.selection.contains(it.uid)
                    })
                    CoroutineScope(Dispatchers.IO).launch {
                        db.seriesDao().deleteAll(*selected.toTypedArray())
                    }
                    libraryAdapter.update(libraryAdapter.dataSet.toMutableList()
                        .apply { removeAll(selected) })
                    actionMode?.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            tracker.clearSelection()
            actionMode = null
        }
    }

    private class LibraryAdapter(dataSet: List<Series>,
        private val glide: RequestManager,
        val navController: NavController) : CustomAdapter<LibraryCardBinding, Series, Long>(dataSet) {

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
            viewType: Int): CustomAdapter<LibraryCardBinding, Series, Long>.ViewHolder {
            val view = LibraryCardBinding.inflate(LayoutInflater.from(parent.context))
            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: CustomAdapter<LibraryCardBinding, Series, Long>.ViewHolder,
            position: Int) {
            holder.bind(dataSet[position])
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

        override fun update(list: List<Series>) {
            val oldItemCount = dataSet.size
            val newItemCount = list.size
            dataSet = list
            notifyItemRangeChanged(0, max(newItemCount, oldItemCount))
        }

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        inner class ViewHolder(view: LibraryCardBinding) : CustomAdapter<LibraryCardBinding, Series, Long>.ViewHolder(
            view) {
            override fun getItem() = object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): Long = dataSet[bindingAdapterPosition].uid
            }

            private inner class SurfaceColor(val colorSurface: Int, val colorSurfaceVariant: Int)

            fun getColor(): SurfaceColor {
                val typedValue = TypedValue()

                val a: TypedArray = view.root.context.obtainStyledAttributes(typedValue.data,
                    intArrayOf(com.google.android.material.R.attr.colorSurface,
                        com.google.android.material.R.attr.colorSurfaceVariant))
                val colorSurface = a.getColor(0, 0)
                val colorSurfaceVariant = a.getColor(a.getIndex(1), 0)
                a.recycle()
                return SurfaceColor(colorSurface, colorSurfaceVariant)
            }

            override fun bind(item: Series) {
                val requestOptions = RequestOptions().transform(FitCenter(),
                    RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        Resources.getSystem().displayMetrics).toInt()))

                tracker.let { selector ->
                    val color = getColor()
                    if (selector.isSelected(item.uid)) {
                        view.root.setCardBackgroundColor(color.colorSurfaceVariant)
                    } else view.root.setCardBackgroundColor(color.colorSurface)
                }
                view.text.text = item.title
                glide.load(item.imageUri)
                    .error(glide.load(R.drawable.baseline_broken_image_24))
                    .apply(requestOptions)
                    .into(view.image)
                view.root.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getInstance(view.root.context)
                        db.seriesDao().update(item.copy(lastAccess = ZonedDateTime.now()))
                    }
                    val direction = LibraryFragmentDirections.actionLibraryToChapterList(item)
                    navController.navigate(direction)
                }
            }
        }

        class ItemsDetailsLookup(private val recyclerView: RecyclerView) : CustomAdapter.ItemsDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                val view = recyclerView.findChildViewUnder(e.x, e.y)
                return (view?.let { recyclerView.getChildViewHolder(it) } as ViewHolder?)?.getItem()
            }
        }

        class ItemsKeyProvider(private val adapter: LibraryAdapter) : CustomAdapter.ItemsKeyProvider<Long>() {
            override fun getKey(position: Int): Long = adapter.dataSet[position].uid
            override fun getPosition(key: Long): Int = adapter.dataSet.indexOfFirst { it.uid == key }
        }

    }

}