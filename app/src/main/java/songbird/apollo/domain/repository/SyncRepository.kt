package songbird.apollo.domain.repository

interface SyncRepository {

    fun syncPlaylist(id: Int)

    fun syncPlaylists()

    fun syncPlaylistSong(playlistId: Int, songId: Int)
}