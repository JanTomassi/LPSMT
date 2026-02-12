package it.unitn.disi.lpsmt.g03.core

import androidx.annotation.IntRange
import androidx.lifecycle.MutableLiveData

interface CustomeActivity {
    var isFullscreen: Boolean

    val progressBarState: MutableLiveData<Int>

    fun hideBars()

    fun hideNavBar()

    fun showBars()

    fun showNavBar()

    fun postProgress(@IntRange(0, 1000) newState: Int)
}