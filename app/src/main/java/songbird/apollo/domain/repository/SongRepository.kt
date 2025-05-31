package songbird.apollo.domain.repository

import songbird.apollo.domain.model.SongPreview

interface SongRepository {

    suspend fun getSongs(): List<SongPreview>

}