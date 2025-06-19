package songbird.apollo.domain.usecase

import songbird.apollo.domain.repository.PlaylistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsSongFavoriteUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    // TODO: 1 - Favorite
    suspend operator fun invoke(songId: Int): Boolean {
        return 1 in playlistRepository.getSongPlaylists(songId)
    }

}