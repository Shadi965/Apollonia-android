package songbird.apollo.domain.repository

import songbird.apollo.domain.model.Album

interface AlbumRepository {

    suspend fun getAlbum(id: Int): Album?

    suspend fun fetchAlbum(id: Int): Album

    suspend fun isAlbumLocal(id: Int): Boolean
}