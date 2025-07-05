package songbird.apollo.presentation.model

import androidx.compose.runtime.Stable
import songbird.apollo.domain.model.Song
import songbird.apollo.domain.model.SongPreview

@Stable
data class SongUi(
    val id: Int,

    val title: String,
    val artist: String,
    val composer: String,

    val albumId: Int,
    val track: Int,
    val disc: Int,

    val date: String,
    val copyright: String,
    val genre: String,
    val duration: Int,

    val coverUrl: String?,

    val isDownloaded: Boolean,
)

@Stable
data class SongPreviewUi(
    val id: Int,
    val title: String,
    val artist: String,
    val albumId: Int,
    val duration: Int,

    val position: Double,
    val coverUrl: String?,
    val isDownloaded: Boolean,
)

fun Song.toUi(isDownloaded: Boolean = false) = SongUi(
    id = id,
    title = title,
    artist = artist,
    composer = composer,
    albumId = albumId,
    track = track,
    disc = disc,
    date = date,
    copyright = copyright,
    genre = genre,
    duration = duration,
    coverUrl = coverUrl,
    isDownloaded = isDownloaded
)

fun SongPreview.toUi() = SongPreviewUi(
    id = id,
    title = title,
    artist = artist,
    albumId = albumId,
    duration = duration,
    position = position,
    coverUrl = coverUrl,
    isDownloaded = false
)
