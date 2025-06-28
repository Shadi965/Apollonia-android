package songbird.apollo.domain.usecase

import kotlinx.coroutines.flow.Flow
import songbird.apollo.domain.model.SongPreview
import songbird.apollo.domain.repository.PlaylistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritesUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {

    operator fun invoke(): Flow<List<SongPreview>> {
        return playlistRepository.getFavorites()
    }

}
