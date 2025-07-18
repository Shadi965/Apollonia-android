package songbird.apollo.data

import songbird.apollo.data.local.SyncStatus
import songbird.apollo.data.local.entity.AlbumEntity
import songbird.apollo.data.local.entity.PlaylistEntity
import songbird.apollo.data.local.entity.PositionedSong
import songbird.apollo.data.local.entity.SongEntity
import songbird.apollo.data.network.URL
import songbird.apollo.data.network.dto.AlbumDto
import songbird.apollo.data.network.dto.LyricsDto
import songbird.apollo.data.network.dto.PlaylistDto
import songbird.apollo.data.network.dto.SongDto
import songbird.apollo.domain.model.Album
import songbird.apollo.domain.model.LyricLine
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

fun SongDto.toEntity() = SongEntity(
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
    duration
)

fun SongDto.toSongPreview() = SongPreview(
    id,
    title,
    artist,
    albumId,
    duration,
    position = 0.0,
    coverUrl = "$URL/album/cover/${albumId}"
)

fun SongEntity.toSong() = Song(
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

fun PositionedSong.toSongPreview() = SongPreview(
    song.id,
    song.title,
    song.artist,
    song.albumId,
    song.duration,
    position,
    coverUrl = "$URL/album/cover/${song.albumId}"
)

fun AlbumDto.toEntity() = AlbumEntity(
    id,
    title,
    artist,
    trackCount,
    discCount,
    date,
    copyright,
    genre
)

fun AlbumDto.toAlbum() = Album(
    id,
    title,
    artist,
    trackCount,
    discCount,
    date,
    copyright,
    genre
)

fun AlbumEntity.toAlbum() = Album(
    id,
    title,
    artist,
    trackCount,
    discCount,
    date,
    copyright,
    genre
)

fun PlaylistDto.toEntity(syncStatus: SyncStatus) = PlaylistEntity(
    id,
    name,
    syncStatus = syncStatus
)

fun LyricsDto.toLyricLineList(): List<LyricLine> = this.lyrics.map {
    LyricLine(it.text, it.time)
}
