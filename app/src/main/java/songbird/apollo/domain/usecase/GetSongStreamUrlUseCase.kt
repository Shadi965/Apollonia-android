package songbird.apollo.domain.usecase

import songbird.apollo.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSongStreamUrlUseCase @Inject constructor(
    private val songRepository: SongRepository
) {

    operator fun invoke(id: Int): String = songRepository.getSongStreamUrl(id)

}