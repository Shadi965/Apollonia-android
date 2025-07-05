package songbird.apollo.domain.usecase

import songbird.apollo.domain.model.LyricLine
import songbird.apollo.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLyricsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {

    suspend operator fun invoke(id: Int): List<LyricLine> = songRepository.getLyrics(id)

}