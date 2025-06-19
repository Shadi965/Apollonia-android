package songbird.apollo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import songbird.apollo.data.local.entity.SongEntity
import songbird.apollo.data.local.entity.SongPreviewTuple

@Dao
interface SongDao {

    @Query("SELECT id, title, artist, album_id FROM songs")
    fun getSongs(): Flow<List<SongPreviewTuple>>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSong(id: Int): SongEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(songs: List<SongEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(song: SongEntity)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM songs WHERE id = :id)")
    suspend fun isSongExists(id: Int): Boolean

}
