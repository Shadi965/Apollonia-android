package songbird.apollo.domain.repository

interface SyncRepository {

    suspend fun syncPlaylist(id: Int)

    suspend fun syncPlaylists()

    suspend fun syncPlaylistSong(playlistId: Int, songId: Int)
}