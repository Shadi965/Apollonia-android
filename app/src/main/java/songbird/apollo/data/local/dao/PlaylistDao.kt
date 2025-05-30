package songbird.apollo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import songbird.apollo.data.local.entity.PlaylistEntity
import songbird.apollo.data.local.entity.PlaylistSongsEntity
import songbird.apollo.data.local.entity.PositionedSong

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylist(id: Int): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlists: List<PlaylistEntity>)

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Query("UPDATE playlists SET id = :newId WHERE id = :id")
    suspend fun changePlaylistId(id: Int, newId: Int)

    @Delete
    suspend fun delete(playlist: PlaylistEntity)



    @Query("""
        SELECT s.id, s.title, s.artist, s.album_id, ps.position
        FROM songs s
        INNER JOIN playlist_songs ps
        ON s.id = ps.song_id
        WHERE ps.playlist_id = :playlistId
    """)
    fun getSongsFromPlaylist(playlistId: Int): Flow<List<PositionedSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: PlaylistSongsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<PlaylistSongsEntity>)

    @Delete
    suspend fun deleteSong(song: PlaylistSongsEntity)

    @Update
    suspend fun updateSong(song: PlaylistSongsEntity)


}