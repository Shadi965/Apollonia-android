package songbird.apollo.domain.usecase

import songbird.apollo.domain.repository.PlaylistRepository
import songbird.apollo.domain.repository.SyncRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveSongFromFavoritesUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val syncRepository: SyncRepository
) {

    suspend operator fun invoke(id: Int) {
        playlistRepository.removeFromFavorites(id)
        syncRepository.syncPlaylistSong(1, id)
    }

}