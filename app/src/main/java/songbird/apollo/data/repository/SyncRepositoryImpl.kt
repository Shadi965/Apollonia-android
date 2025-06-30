package songbird.apollo.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import songbird.apollo.data.BackendException
import songbird.apollo.data.ParseBackendResponseException
import songbird.apollo.data.di.SingletonScope
import songbird.apollo.data.local.SyncStatus
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.local.dao.PlaylistDao
import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.local.entity.PlaylistEntity
import songbird.apollo.data.local.entity.PlaylistSongsEntity
import songbird.apollo.data.network.api.AlbumApi
import songbird.apollo.data.network.api.PlaylistApi
import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.requests.AlbumsIdsRequest
import songbird.apollo.data.network.requests.SongsIdsRequest
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toEntity
import songbird.apollo.domain.repository.SyncRepository
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    @SingletonScope private val singletonScope: CoroutineScope,
    private val songDao: SongDao,
    private val songApi: SongApi,
    private val albumDao: AlbumDao,
    private val albumApi: AlbumApi,
    private val playlistDao: PlaylistDao,
    private val playlistApi: PlaylistApi
) : SyncRepository {

    override fun syncPlaylist(id: Int) {
        singletonScope.launch {
            val playlist = playlistDao.getPlaylist(id).first()
            if (playlist != null)
                syncPlaylist(playlist)
            else
                loadPlaylist(id)
            loadPlaylistSongs(id)
        }
    }

    override fun syncPlaylists() {
        singletonScope.launch {
            try {
                playlistDao.getChangedPlaylists().forEach{ syncPlaylist(it) }
                syncPlaylistSongs()
                loadPlaylists()
            } catch (_: BackendException) {
                // TODO: Из-за отдельного диспатчера
            } catch (_: ConnectException) {
            } catch (_: ParseBackendResponseException) {
            }
        }
    }

    override fun syncPlaylistSong(playlistId: Int, songId: Int) {
        singletonScope.launch {
            val playlistSong = playlistDao.getPlaylistSong(playlistId, songId) ?: return@launch
            syncPlaylistSong(playlistSong)
        }
    }

    private suspend fun syncPlaylistSongs() {
        playlistDao.getChangedPlaylistSongs().forEach { syncPlaylistSong(it) }
    }

    private suspend fun syncPlaylist(playlist: PlaylistEntity) {
        // TODO: Между Api и репозиторием нужен source слой
        try {
            when (playlist.syncStatus) {
                SyncStatus.CREATED -> {
                    wrapRetrofitExceptions {
                        playlistApi.newPlaylist(playlist.name)
                    }
                    playlistDao.update(playlist.copy(syncStatus = SyncStatus.SYNCED))
                }

                SyncStatus.UPDATED -> {
                    wrapRetrofitExceptions {
                        playlistApi.changePlaylistName(playlist.id, playlist.name)
                    }
                    playlistDao.update(playlist.copy(syncStatus = SyncStatus.SYNCED))
                }

                SyncStatus.DELETED -> {
                    wrapRetrofitExceptions {
                        playlistApi.deletePlaylist(playlist.id)
                    }
                    playlistDao.delete(playlist)
                }

                else -> Unit
            }
        } catch (ex: BackendException) {
            return
        } catch (ex: ConnectException) {
            return
        }
    }

    private suspend fun syncPlaylistSong(playlistSong: PlaylistSongsEntity) {
        // TODO: Между Api и репозиторием нужен source слой
        try {
            when(playlistSong.syncStatus) {
                SyncStatus.CREATED -> {
                    wrapRetrofitExceptions {
                        playlistApi.addSongToPlaylist(playlistSong.playlistId, playlistSong.songId, playlistSong.position!!)
                    }
                    playlistDao.updateSong(playlistSong.copy(syncStatus = SyncStatus.SYNCED))
                }
                SyncStatus.UPDATED -> {
                    wrapRetrofitExceptions {
                        playlistApi.updateSongPosition(playlistSong.playlistId, playlistSong.songId, playlistSong.position!!)
                    }
                    playlistDao.updateSong(playlistSong.copy(syncStatus = SyncStatus.SYNCED))

                }
                SyncStatus.DELETED -> {
                    wrapRetrofitExceptions {
                        playlistApi.removeSongFromPlaylist(playlistSong.playlistId, playlistSong.songId)
                    }
                    playlistDao.deleteSong(playlistSong)
                }
                else -> Unit
            }
        } catch (ex: BackendException) {
            if (ex.code == 0)
                return
        } catch (ex: ConnectException) {
            return
        }
    }

    private suspend fun loadPlaylists() = wrapRetrofitExceptions {
        val playlists = playlistApi.getPlaylists().data!!
        playlists.forEach {
            playlistDao.insert(it.toEntity(SyncStatus.SYNCED))
            loadPlaylistSongs(it.id)
        }
    }

    private suspend fun loadPlaylist(id: Int) = wrapRetrofitExceptions {
        val playlist = playlistApi.getPlaylist(id).data!!
        playlistDao.insert(playlist.toEntity(SyncStatus.SYNCED))
    }

    private suspend fun loadPlaylistSongs(playlistId: Int) = wrapRetrofitExceptions {
        val remoteSongs = playlistApi.getSongs(playlistId).data!!
        val localSongs = playlistDao.getPlaylistSongs(playlistId).map { it.songId }.toSet()
        val newSongs = remoteSongs.filter { it.songId !in localSongs }
        val songs = newSongs.filter { !songDao.isSongExists(it.songId) }
            .run {
                songApi.getSongs(SongsIdsRequest(this.map { it.songId })).data!!
                    .map { it.toEntity() }
            }

        val albums = songs.map { it.albumId }
            .toSet()
            .filter { !albumDao.isAlbumExists(it) }
            .run {
                albumApi.getAlbums(AlbumsIdsRequest(this.toList())).data!!
                    .map { it.toEntity() }
            }
        albumDao.insert(albums)
        songDao.insert(songs)
        playlistDao.insertSongs(newSongs.map { PlaylistSongsEntity(playlistId, it.songId, it.position, SyncStatus.SYNCED) })
    }

}