package songbird.apollo.data.repository

import songbird.apollo.data.network.api.SongApi
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toSongPreview
import songbird.apollo.domain.model.SongPreview
import songbird.apollo.domain.repository.SongRepository
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl(
    private val songApi: SongApi
) : SongRepository {

    override suspend fun getSongs(): List<SongPreview> = wrapRetrofitExceptions {
        songApi.getSongs().data.map { it.toSongPreview() }
    }
}