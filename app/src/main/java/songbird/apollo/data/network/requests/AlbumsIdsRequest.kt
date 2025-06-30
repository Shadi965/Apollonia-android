package songbird.apollo.data.network.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumsIdsRequest(
    @Json(name = "albums_ids")
    val albumsIds: List<Int>
)
