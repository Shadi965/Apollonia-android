package songbird.apollo.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistDto(
    val id: Int,
    val name: String,
)
