package songbird.apollo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import songbird.apollo.data.local.entity.AlbumEntity
import songbird.apollo.data.local.entity.AlbumPreviewTuple
import songbird.apollo.data.local.entity.SongPreviewTuple

@Dao
interface AlbumDao {
    @Query("SELECT id, title, artist FROM albums")
    fun getAlbums(): Flow<List<AlbumPreviewTuple>>

    @Query("SELECT * FROM albums WHERE id = :id")
    fun getAlbum(id: Int): AlbumEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: AlbumEntity)

    @Query("DELETE FROM albums WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT id, title, artist, album_id, duration FROM songs WHERE album_id = :albumId")
    fun getSongsFromAlbum(albumId: Int): Flow<List<SongPreviewTuple>>

    @Query("SELECT EXISTS(SELECT 1 FROM albums WHERE id = :id)")
    suspend fun isAlbumExists(id: Int): Boolean
}