package songbird.apollo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import songbird.apollo.data.local.SyncStatus

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,

    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus
)