package songbird.apollo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val artist: String,

    @ColumnInfo(name = "track_count") val trackCount: Int,
    @ColumnInfo(name = "disc_count") val discCount: Int,

    val date: String,
    val copyright: String,
    val genre: String
)

data class AlbumPreviewTuple(
    val id: Int,
    val title: String,
    val artist: String
)