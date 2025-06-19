package songbird.apollo.domain.repository

import kotlinx.coroutines.flow.Flow
import songbird.apollo.domain.model.Album

interface AlbumRepository {

    fun getAlbum(id: Int): Flow<Album?>
}