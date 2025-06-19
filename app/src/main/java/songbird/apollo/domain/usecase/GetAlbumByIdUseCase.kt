package songbird.apollo.domain.usecase

import kotlinx.coroutines.flow.Flow
import songbird.apollo.domain.model.Album
import songbird.apollo.domain.repository.AlbumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbumByIdUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {

    operator fun invoke(id: Int): Flow<Album?> {
        return albumRepository.getAlbum(id)
    }

}