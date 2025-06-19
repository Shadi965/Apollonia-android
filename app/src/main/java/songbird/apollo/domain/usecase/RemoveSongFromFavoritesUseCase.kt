package songbird.apollo.domain.usecase

import songbird.apollo.domain.repository.PlaylistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveSongFromFavoritesUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {

    suspend operator fun invoke(id: Int) {
        playlistRepository.removeFromFavorites(id)
    }

}