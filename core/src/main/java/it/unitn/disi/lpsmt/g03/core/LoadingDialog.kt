package it.unitn.disi.lpsmt.g03.core

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import it.unitn.disi.lpsmt.g03.core.databinding.LoadingDialogBinding

class LoadingDialog : DialogFragment() {

    private lateinit var mBinding: LoadingDialogBinding

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = LoadingDialogBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
    }

    fun updatePageNum(page: Int) {
        if (this::mBinding.isInitialized) mBinding.progress.text = resources.getString(R.string.page_loaded,
            page)
    }
}