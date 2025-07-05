package songbird.apollo.domain.repository

import songbird.apollo.domain.model.LyricLine
import songbird.apollo.domain.model.Song
import songbird.apollo.domain.model.SongPreview

interface SongRepository {

    suspend fun getSongs(): List<SongPreview>

    suspend fun searchSongs(query: String): List<SongPreview>

    suspend fun isSongLocal(id: Int): Boolean

    suspend fun getSong(id: Int): Song

    suspend fun fetchSong(id: Int): Song

    fun getSongStreamUrl(songId: Int): String

    suspend fun getLyrics(songId: Int): List<LyricLine>
}