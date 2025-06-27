package songbird.apollo.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import songbird.apollo.data.BackendException
import songbird.apollo.domain.model.SongPreview
import songbird.apollo.domain.repository.PlaylistRepository
import songbird.apollo.domain.repository.SyncRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritesUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val syncRepository: SyncRepository
) {

    operator fun invoke(): Flow<List<SongPreview>> {

        // TODO: Разобраться с диспатчером
        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncRepository.syncPlaylist(1)
            } catch (ex: BackendException) {
                return@launch
            }
        }

        return playlistRepository.getFavorites()
    }

}
