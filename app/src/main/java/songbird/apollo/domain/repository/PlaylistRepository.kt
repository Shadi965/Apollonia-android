package songbird.apollo.domain.repository

import kotlinx.coroutines.flow.Flow
import songbird.apollo.domain.model.SongPreview

interface PlaylistRepository {

    fun getFavorites(): Flow<List<SongPreview>>

    fun addToFavorites(songId: Int)

    fun removeFromFavorites(songId: Int)

    suspend fun getSongPlaylists(songId: Int): List<Int>

}