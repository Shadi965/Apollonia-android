package songbird.apollo.domain.usecase

import songbird.apollo.domain.model.Song
import songbird.apollo.domain.repository.SongRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSongByIdUseCase @Inject constructor(
    private val songRepository: SongRepository
) {

    suspend operator fun invoke(id: Int): Song {
        return if (songRepository.isSongLocal(id)) songRepository.getSong(id)
        else songRepository.fetchSong(id)
    }

}
