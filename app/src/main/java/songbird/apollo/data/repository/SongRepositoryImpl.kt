package songbird.apollo.data.repository

import songbird.apollo.data.local.dao.SongDao
import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toSong
import songbird.apollo.data.toSongPreview
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

    override suspend fun getSongs(): List<SongPreview> = wrapRetrofitExceptions {
        songApi.getSongs().data!!.map { it.toSongPreview() }
    }

    override suspend fun searchSongs(query: String): List<SongPreview> = wrapRetrofitExceptions {
        songApi.searchSongs(query).data!!.map { it.toSongPreview() }
    }

    override suspend fun isSongLocal(id: Int): Boolean {
        return songDao.isSongExists(id)
    }

    override suspend fun getSong(id: Int): Song {
        // TODO: Nullable
        return songDao.getSong(id)!!.toSong()
    }

    override suspend fun fetchSong(id: Int): Song = wrapRetrofitExceptions {
        songApi.getSong(id).data!!.toSong()
    }

}