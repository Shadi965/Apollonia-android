package songbird.apollo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import songbird.apollo.data.local.SyncStatus
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.local.dao.PlaylistDao
import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.local.entity.PlaylistEntity
import songbird.apollo.data.local.entity.PlaylistSongsEntity
import songbird.apollo.data.network.api.AlbumApi
import songbird.apollo.data.network.api.PlaylistApi
import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.dto.AlbumDto
import songbird.apollo.data.network.dto.PlaylistDto
import songbird.apollo.data.network.dto.SongDto
import songbird.apollo.data.network.dto.SongPositionDto
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

    override suspend fun addToFavorites(songId: Int) {
        val song = songApi.getSong(songId).data!!
        val album = albumApi.getAlbum(song.albumId).data!!
        addNewAlbum(album)
        addNewSong(song)
        var pos = playlistDao.minPosition(1) ?: 2.0
        --pos;
        playlistDao.insertSong(PlaylistSongsEntity(1, songId, pos, SyncStatus.CREATED))
    }

    override suspend fun removeFromFavorites(songId: Int) {
        playlistDao.updateSong(PlaylistSongsEntity(1, songId, null, SyncStatus.DELETED))
    }

    // TODO: Не забыть поправить синхронизацию
    override suspend fun syncPlaylists() {
        val localPlaylists = playlistDao.getPlaylists().first()

        syncCreatedPlaylists(localPlaylists)
        syncUpdatedPlaylists(localPlaylists)
        syncDeletedPlaylists(localPlaylists)

        syncPlaylistSongs()

        loadFromServer()
    }

    private suspend fun syncCreatedPlaylists(localPlaylists: List<PlaylistEntity>) {
        for (playlist in localPlaylists.filter { it.syncStatus == SyncStatus.CREATED }) {
            val response = playlistApi.newPlaylist(playlist.name)
            if (response.status == "success") {
                playlistDao.changePlaylistId(playlist.id, response.data!!.id)
                playlistDao.update(
                    playlist.copy(
                        id = response.data.id,
                        syncStatus = SyncStatus.SYNCED
                    )
                )
            }
        }

    }

    private suspend fun syncUpdatedPlaylists(localPlaylists: List<PlaylistEntity>) {
        for (playlist in localPlaylists.filter { it.syncStatus == SyncStatus.UPDATED }) {
            val response = playlistApi.changePlaylistName(playlist.id, playlist.name)
            if (response.status == "success")
                playlistDao.update(playlist.copy(syncStatus = SyncStatus.SYNCED))
        }
    }

    private suspend fun syncDeletedPlaylists(localPlaylists: List<PlaylistEntity>) {
        for (playlist in localPlaylists.filter { it.syncStatus == SyncStatus.DELETED }) {
            try {
                playlistApi.deletePlaylist(playlist.id)
                playlistDao.delete(playlist)
            } catch (e: HttpException) {
                if (e.code() == 404)
                    playlistDao.delete(playlist)
                else throw e
            }
        }
    }

    private suspend fun syncPlaylistSongs() {
        val playlists = playlistDao.getPlaylists().first()
        for (playlist in playlists) {
            val songs = playlistDao.getPlaylistSongs(playlist.id)
            for (song in songs) {
                when (song.syncStatus) {
                    // TODO: Позиции необходимо дополнительно проверять
                    SyncStatus.CREATED -> {
                        val response =
                            playlistApi.addSongToPlaylist(playlist.id, song.songId, song.position!!)
                        if (response.status == "success")
                            playlistDao.updateSong(
                                song.copy(
                                    syncStatus = SyncStatus.SYNCED
                                )
                            )
                    }

                    SyncStatus.UPDATED -> {
                        val response =
                            playlistApi.updateSongPosition(playlist.id, song.songId, song.position!!)
                        if (response.status == "success")
                            playlistDao.updateSong(
                                song.copy(
                                    syncStatus = SyncStatus.SYNCED
                                )
                            )
                    }

                    SyncStatus.DELETED -> {
                        try {
                            playlistApi.removeSongFromPlaylist(playlist.id, song.songId)
                            playlistDao.deleteSong(song)
                        } catch (e: HttpException) {
                            if (e.code() == 404)
                                playlistDao.deleteSong(song)
                            else throw e
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private suspend fun loadFromServer() {
        val remotePlaylists = playlistApi.getPlaylists().data!!
        val localPlaylists = playlistDao.getPlaylists().first()

        removeDeletedPlaylists(localPlaylists, remotePlaylists)
        updateLocalPlaylists(localPlaylists, remotePlaylists)
        syncSongsInPlaylists(remotePlaylists)
    }

    private suspend fun removeDeletedPlaylists(
        local: List<PlaylistEntity>,
        remote: List<PlaylistDto>
    ) {
        val remoteIds = remote.map { it.id }.toSet()
        val toDelete = local.filter { it.syncStatus == SyncStatus.SYNCED && it.id !in remoteIds }

        for (playlist in toDelete)
            playlistDao.delete(playlist)
    }

    private suspend fun updateLocalPlaylists(
        local: List<PlaylistEntity>,
        remote: List<PlaylistDto>
    ) {
        val localMap = local.associateBy { it.id }

        val changed = remote.filter { it.id in localMap && localMap[it.id]!!.name != it.name }.map { it.toEntity(SyncStatus.SYNCED) }
        val new = remote.filter { it.id !in localMap }.map { it.toEntity(SyncStatus.SYNCED) }

        changed.forEach { playlistDao.update(it) }
        playlistDao.insert(new)
    }

    private suspend fun updatePlaylistSongs(
        playlistId: Int,
        local: List<PlaylistSongsEntity>,
        remote: List<SongPositionDto>
    ) {
        val localMap = local.associateBy { it.songId }
        val changed = remote.filter { localMap[it.songId]?.position != it.position }.map { PlaylistSongsEntity(
            playlistId = playlistId,
            songId = it.songId,
            position = it.position,
            syncStatus = SyncStatus.SYNCED
        ) }
        val new = remote.filter { it.songId !in localMap }.map { PlaylistSongsEntity(
            playlistId = playlistId,
            songId = it.songId,
            position = it.position,
            syncStatus = SyncStatus.SYNCED
        ) }

        changed.forEach { playlistDao.updateSong(it) }
        playlistDao.insertSongs(new)
    }

    private suspend fun syncSongsInPlaylists(remotePlaylists: List<PlaylistDto>) {
        for (playlist in remotePlaylists) {
            val remoteSongs = playlistApi.getSongs(playlist.id).data!!
            val localSongs = playlistDao.getPlaylistSongs(playlist.id)
            val remoteIds = remoteSongs.map { it.songId }.toSet()

            // TODO: Мегабардак
            for (songId in remoteIds) {
                val song = songApi.getSong(songId).data!!
                val album = albumApi.getAlbum(song.albumId).data!!
                addNewAlbum(album)
                addNewSong(song)
            }

            updatePlaylistSongs(playlist.id, localSongs, remoteSongs)

            val toDelete =
                localSongs.filter { it.syncStatus == SyncStatus.SYNCED && it.songId !in remoteIds }
            for (song in toDelete)
                playlistDao.deleteSong(song)
        }
    }

    private suspend fun addNewAlbum(
        remote: AlbumDto
    ) {
        if (!albumDao.isAlbumExists(remote.id))
            albumDao.insert(remote.toEntity())
    }

    private suspend fun addNewSong(
        remote: SongDto
    ) {
        if (!songDao.isSongExists(remote.id))
            songDao.insert(remote.toEntity())
    }
}