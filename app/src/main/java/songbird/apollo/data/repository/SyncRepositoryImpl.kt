package songbird.apollo.data.repository

import kotlinx.coroutines.flow.first
import songbird.apollo.data.BackendException
import songbird.apollo.data.local.SyncStatus
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.local.dao.PlaylistDao
import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.local.entity.PlaylistEntity
import songbird.apollo.data.local.entity.PlaylistSongsEntity
import songbird.apollo.data.network.api.AlbumApi
import songbird.apollo.data.network.api.PlaylistApi
import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toEntity
import songbird.apollo.domain.repository.SyncRepository
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val songApi: SongApi,
    private val albumDao: AlbumDao,
    private val albumApi: AlbumApi,
    private val playlistDao: PlaylistDao,
    private val playlistApi: PlaylistApi
) : SyncRepository {

    override suspend fun syncPlaylist(id: Int) {
        val playlist = playlistDao.getPlaylist(id).first()
        if (playlist != null)
            syncPlaylist(playlist)
        else
            loadPlaylist(id)
        loadPlaylistSongs(id)
    }


    override suspend fun syncPlaylists() {
        playlistDao.getChangedPlaylists().forEach{ syncPlaylist(it) }
        syncPlaylistSongs()
        // TODO: Загрузка списка плейлистов с сервера
    }

    override suspend fun syncPlaylistSong(playlistId: Int, songId: Int) {
        val playlistSong = playlistDao.getPlaylistSong(playlistId, songId) ?: return
        syncPlaylistSong(playlistSong)
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

    private suspend fun loadPlaylist(id: Int) = wrapRetrofitExceptions {
        val playlist = playlistApi.getPlaylist(id).data!!
        playlistDao.insert(playlist.toEntity(SyncStatus.SYNCED))
    }

    private suspend fun loadPlaylistSongs(playlistId: Int) = wrapRetrofitExceptions {
        val remoteSongs = playlistApi.getSongs(playlistId).data!!
        val localSongs = playlistDao.getPlaylistSongs(playlistId).map { it.songId }.toSet()
        val newSongs = remoteSongs.filter { it.songId !in localSongs }
        newSongs.forEach {
            if (!songDao.isSongExists(it.songId)) {
                val song = songApi.getSong(it.songId).data!!
                if (!albumDao.isAlbumExists(song.albumId)) {
                    val album = albumApi.getAlbum(song.albumId).data!!
                    albumDao.insert(album.toEntity())
                }
                songDao.insert(song.toEntity())
            }
            playlistDao.insertSong(PlaylistSongsEntity(playlistId, it.songId, it.position, SyncStatus.SYNCED))
        }
    }

}