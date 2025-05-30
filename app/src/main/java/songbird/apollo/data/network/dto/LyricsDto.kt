package songbird.apollo.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LyricsDto(
    @Json(name = "song_id")
    val songId: Int,
    val lyrics: List<LineDto>,
)

@JsonClass(generateAdapter = true)
data class LineDto(
    val time: Int,
    val text: String,
)
