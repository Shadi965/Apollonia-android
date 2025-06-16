package songbird.apollo.domain.repository

import kotlinx.coroutines.flow.Flow
import songbird.apollo.domain.model.SongPreview

interface PlaylistRepository {

    fun getFavorites(): Flow<List<SongPreview>>

    suspend fun syncPlaylists()

}