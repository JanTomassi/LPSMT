package it.unitn.disi.lpsmt.g03.library

data class LibraryEntry(
    var seriesTitle: String? = null,
    var seriesStatus: ReadingState? = null,
    var seriesIsOnline: Boolean? = null,
    var seriesIsOneShot: Boolean? = null,

    var chapterTitle: String? = null,
    var chapterNum: Int? = null,
    var chapterCurrentPage: Int? = null,
    var chapterState: ReadingState? = null,
)