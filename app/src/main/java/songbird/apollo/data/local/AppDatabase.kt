package songbird.apollo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.local.dao.PlaylistDao
import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.local.entity.AlbumEntity
import songbird.apollo.data.local.entity.PlaylistEntity
import songbird.apollo.data.local.entity.PlaylistSongsEntity
import songbird.apollo.data.local.entity.SongEntity
import javax.inject.Singleton

@Database(
    entities = [
        AlbumEntity::class,
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongsEntity::class
    ],
    version = 1
)
@TypeConverters(SyncStatusConverter::class)
@Singleton
abstract class AppDatabase : RoomDatabase() {


    abstract fun albumDao(): AlbumDao

    abstract fun songDao(): SongDao

    abstract fun playlistDao(): PlaylistDao
}