package songbird.apollo.data.network.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongsIdsRequest(
    @Json(name = "songs_ids")
    val songsIds: List<Int>
)
