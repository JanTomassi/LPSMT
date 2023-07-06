package it.unitn.disi.lpsmt.g03.tracking

data class TrackerEntry (
    var seriesTitle: String? = null,
    var seriesStatus: ReadingState? = null,
    var seriesIsOnline: Boolean? = null,
    var seriesIsOneShot: Boolean? = null,

    var chapterTitle: String? = null,
    var chapterNum: Int? = null
)