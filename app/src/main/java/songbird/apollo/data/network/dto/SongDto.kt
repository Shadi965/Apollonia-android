package songbird.apollo.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongDto(
    val id: Int,
    val title: String,
    val artist: String,
    val composer: String,

    @Json(name = "album_id")
    val albumId: Int,
    val track: Int,
    val disc: Int,

    val date: String,
    val copyright: String,
    val genre: String,

    val duration: Int,
    val bitrate: Int,
    val channels: Int,
    @Json(name = "sample_rate")
    val sampleRate: Int,
)

@JsonClass(generateAdapter = true)
data class SongPositionDto(
    @Json(name = "song_id")
    val songId: Int,
    val position: Double
)
