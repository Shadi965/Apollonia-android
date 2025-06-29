package songbird.apollo.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.network.api.AlbumApi
import songbird.apollo.data.network.wrapRetrofitExceptions
import songbird.apollo.data.toAlbum
import songbird.apollo.domain.model.Album
import songbird.apollo.domain.repository.AlbumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao,
    private val albumApi: AlbumApi
) : AlbumRepository {
    override suspend fun getAlbum(id: Int): Album? = withContext(Dispatchers.IO) {
        return@withContext albumDao.getAlbum(id)?.toAlbum()
    }

    override suspend fun fetchAlbum(id: Int): Album = withContext(Dispatchers.IO) {
        return@withContext wrapRetrofitExceptions {
            albumApi.getAlbum(id).data!!.toAlbum()
        }
    }

    override suspend fun isAlbumLocal(id: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext albumDao.isAlbumExists(id)
    }

}