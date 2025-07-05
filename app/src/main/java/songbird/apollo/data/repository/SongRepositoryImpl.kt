package songbird.apollo.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.network.URL
import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.dto.SongDto
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toLyricLineList
import songbird.apollo.data.toSong
import songbird.apollo.data.toSongPreview
import songbird.apollo.domain.model.LyricLine
import songbird.apollo.domain.model.Song
import songbird.apollo.domain.model.SongPreview
import songbird.apollo.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songApi: SongApi,
    private val songDao: SongDao
) : SongRepository {

    override suspend fun getSongs(): List<SongPreview> = withContext(Dispatchers.IO) {
        return@withContext wrapRetrofitExceptions {
            songApi.getSongs().data!!
                // TODO: Это убрать
                .groupBy { it.albumId }
                .toList()
                .sortedBy { (_, songs) -> songs.firstOrNull()?.artist ?: "" }
                .flatMap { (_, songs) ->
                    songs.sortedWith(
                        compareBy<SongDto> { it.disc }
                            .thenBy { it.track }
                    )
                }
                .map { it.toSongPreview() }
        }
    }

    override suspend fun searchSongs(query: String): List<SongPreview> = withContext(Dispatchers.IO) {
        return@withContext wrapRetrofitExceptions {
            songApi.searchSongs(query).data!!.map { it.toSongPreview() }
        }
    }

    override suspend fun isSongLocal(id: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext songDao.isSongExists(id)
    }

    override suspend fun getSong(id: Int): Song = withContext(Dispatchers.IO) {
        // TODO: Nullable
        return@withContext songDao.getSong(id)!!.toSong()
    }

    override suspend fun fetchSong(id: Int): Song = withContext(Dispatchers.IO) {
        return@withContext wrapRetrofitExceptions {
            songApi.getSong(id).data!!.toSong()
        }
    }

    override fun getSongStreamUrl(songId: Int): String {
        return "$URL/stream/$songId"
    }

    override suspend fun getLyrics(songId: Int): List<LyricLine> = withContext(Dispatchers.IO) {
        return@withContext wrapRetrofitExceptions {
            songApi.getSongLyrics(songId).data!!.toLyricLineList()
        }
    }

}