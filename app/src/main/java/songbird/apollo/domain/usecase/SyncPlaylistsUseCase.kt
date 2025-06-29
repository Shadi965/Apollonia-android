package songbird.apollo.domain.usecase

import songbird.apollo.domain.repository.SyncRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncPlaylistsUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {

    operator fun invoke() {
        syncRepository.syncPlaylists()
    }

}