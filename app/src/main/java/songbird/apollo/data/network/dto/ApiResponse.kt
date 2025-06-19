package songbird.apollo.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val status: String,
    val data: T?,
    val message: String?,
)

@JsonClass(generateAdapter = true)
class EmptyResponse
