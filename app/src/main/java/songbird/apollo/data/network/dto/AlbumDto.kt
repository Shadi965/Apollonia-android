package songbird.apollo.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumDto(
    val id: Int,
    val title: String,
    val artist: String,

    @Json(name = "track_count")
    val trackCount: Int,
    @Json(name = "disc_count")
    val discCount: Int,
    val compilation: Boolean,

    val date: String,
    val copyright: String,
    val genre: String,
)
