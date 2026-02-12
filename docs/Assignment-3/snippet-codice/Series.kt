@Entity(indices = [Index(value = ["title"], unique = true)])
data class Series(@ColumnInfo("title") val title: String,
    @ColumnInfo("status") val status: ReadingState,
    @ColumnInfo("is one device") val isOnDevice: Boolean,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("chapters") val chapters: Int?,
    @ColumnInfo("image url") val imageUri: Uri?,
    @ColumnInfo("is one-shot") val isOne_shot: Boolean,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime,
    @ColumnInfo("last chapter read") val lastChapterRead: Int,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)
