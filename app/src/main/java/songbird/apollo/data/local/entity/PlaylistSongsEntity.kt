package songbird.apollo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import songbird.apollo.data.local.SyncStatus

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlist_id", "song_id"],
    indices = [
        Index("song_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistSongsEntity(
    @ColumnInfo(name = "playlist_id") val playlistId: Int,
    @ColumnInfo(name = "song_id") val songId: Int,

    val position: Double?,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus
)
