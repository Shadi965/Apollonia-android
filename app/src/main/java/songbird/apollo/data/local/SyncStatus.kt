package songbird.apollo.data.local

import androidx.room.TypeConverter

enum class SyncStatus {
    SYNCED,
    CREATED,
    UPDATED,
    DELETED
}

class SyncStatusConverter {
    @TypeConverter
    fun fromStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toStatus(status: String): SyncStatus = SyncStatus.valueOf(status)
}