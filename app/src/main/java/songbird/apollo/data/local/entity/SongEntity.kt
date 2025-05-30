package songbird.apollo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("album_id")
    ]
)
data class SongEntity(
    @PrimaryKey
    val id: Int,

    val title: String,
    val artist: String,
    val composer: String,

    @ColumnInfo(name = "album_id")
    val albumId: Int,
    val track: Int,
    val disc: Int,

    val date: String,
    val copyright: String,
    val genre: String,

    val duration: Int
)

data class SongPreviewTuple(
    val id: Int,
    val title: String,
    val artist: String,
    @ColumnInfo(name = "album_id")
    val albumId: Int
)

data class PositionedSong(
    @Embedded
    val song: SongPreviewTuple,
    val position: Double
)