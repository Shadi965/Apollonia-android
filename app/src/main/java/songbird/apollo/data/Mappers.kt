package songbird.apollo.data

import songbird.apollo.data.network.URL
import songbird.apollo.data.network.dto.SongDto
import songbird.apollo.domain.model.Song
import songbird.apollo.domain.model.SongPreview

fun SongDto.toSong() = Song(
    id,
    title,
    artist,
    composer,
    albumId,
    track,
    disc,
    date,
    copyright,
    genre,
    duration,
    coverUrl = "$URL/album/cover/${albumId}"
)

fun SongDto.toSongPreview() = SongPreview(
    id,
    title,
    artist,
    albumId,
    position = 0.0,
    coverUrl = "$URL/album/cover/${albumId}"
)