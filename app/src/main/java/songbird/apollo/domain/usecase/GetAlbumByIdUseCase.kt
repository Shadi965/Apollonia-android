package songbird.apollo.domain.usecase

import songbird.apollo.domain.model.Album
import songbird.apollo.domain.repository.AlbumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbumByIdUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {

    suspend operator fun invoke(id: Int): Album {

        return if (albumRepository.isAlbumLocal(id)) albumRepository.getAlbum(id)!!
        else albumRepository.fetchAlbum(id)
    }

}