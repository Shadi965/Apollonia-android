package songbird.apollo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import songbird.apollo.data.local.dao.AlbumDao
import songbird.apollo.data.toAlbum
import songbird.apollo.domain.model.Album
import songbird.apollo.domain.repository.AlbumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao
) : AlbumRepository {
    override fun getAlbum(id: Int): Flow<Album?> {
        return albumDao.getAlbum(id).map { it?.toAlbum() }
    }

}