package songbird.apollo.data.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import songbird.apollo.data.network.dto.ApiResponse
import songbird.apollo.data.network.dto.LyricsDto
import songbird.apollo.data.network.dto.SongDto
import songbird.apollo.data.network.requests.SongsIdsRequest

interface SongApi {

    @GET("songs/")
    suspend fun getSongs(): ApiResponse<List<SongDto>>

    @GET("song/{song_id}")
    suspend fun getSong(
        @Path("song_id") songId: Int,
    ): ApiResponse<SongDto>

    @POST("songs/list")
    suspend fun getSongs(
        @Body songsIds: SongsIdsRequest
    ): ApiResponse<List<SongDto>>

    @GET("lyrics/{song_id}")
    suspend fun getSongLyrics(
        @Path("song_id") songId: Int,
    ): ApiResponse<LyricsDto>

    @GET("songs/search/")
    suspend fun searchSongs(
        @Query("query") query: String,
    ): ApiResponse<List<SongDto>>

}