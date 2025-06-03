package songbird.apollo.presentation.model

import songbird.apollo.domain.model.Song
import songbird.apollo.domain.model.SongPreview

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

data class SongPreviewUi(
    val id: Int,
    val title: String,
    val artist: String,
    val albumId: Int,
    val position: Double,
    val coverUrl: String?,
    val isDownloaded: Boolean,
)

fun Song.toUi() = SongUi(
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
    isDownloaded = false
)

fun SongPreview.toUi() = SongPreviewUi(
    id = id,
    title = title,
    artist = artist,
    albumId = albumId,
    position = position,
    coverUrl = coverUrl,
    isDownloaded = false
)
