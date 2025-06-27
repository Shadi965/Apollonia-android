package songbird.apollo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import songbird.apollo.data.local.SyncStatus
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.local.dao.PlaylistDao
import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.local.entity.PlaylistSongsEntity
import songbird.apollo.data.network.api.AlbumApi
import songbird.apollo.data.network.api.PlaylistApi
import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toEntity
import songbird.apollo.data.toSongPreview
import songbird.apollo.domain.model.SongPreview
import songbird.apollo.domain.repository.PlaylistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val playlistApi: PlaylistApi,
    private val songApi: SongApi,
    private val albumApi: AlbumApi
) : PlaylistRepository {

    override fun getFavorites(): Flow<List<SongPreview>> {
        // TODO: Любимое, в идеале, должно быть отдельной таблицей
        return playlistDao.getSongsFromPlaylist(1).map { songs ->
            songs.map { it.toSongPreview() }
        }
    }

    override suspend fun getSongPlaylists(songId: Int): List<Int> {
        return playlistDao.getSongPlaylists(songId)
    }

    override suspend fun addToFavorites(songId: Int) = wrapRetrofitExceptions {
        if (!songDao.isSongExists(songId)) {
            val song = songApi.getSong(songId).data!!
            val album = albumApi.getAlbum(song.albumId).data!!

            albumDao.insert(album.toEntity())
            songDao.insert(song.toEntity())
        }

        var pos = playlistDao.minPosition(1) ?: 2.0
        --pos;
        playlistDao.insertSong(PlaylistSongsEntity(1, songId, pos, SyncStatus.CREATED))
    }

    override suspend fun removeFromFavorites(songId: Int) {
        playlistDao.updateSong(PlaylistSongsEntity(1, songId, null, SyncStatus.DELETED))
    }

}