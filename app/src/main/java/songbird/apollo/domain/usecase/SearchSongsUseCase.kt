package songbird.apollo.domain.usecase

import songbird.apollo.domain.model.SongPreview
import songbird.apollo.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {

    suspend operator fun invoke(query: String): List<SongPreview> {
        return songRepository.searchSongs(query)
    }

}