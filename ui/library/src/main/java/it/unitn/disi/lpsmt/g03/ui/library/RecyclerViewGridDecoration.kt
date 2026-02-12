package it.unitn.disi.lpsmt.g03.ui.library

import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewGridDecoration(private val spanCount: Int,
    space: Int,
    private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    private val dp: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        space.toFloat(),
        Resources.getSystem().displayMetrics).toInt()

    override fun getItemOffsets(outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {

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