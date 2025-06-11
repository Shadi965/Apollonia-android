package songbird.apollo.data.network.api

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import songbird.apollo.data.network.dto.ApiResponse
import songbird.apollo.data.network.dto.EmptyResponse
import songbird.apollo.data.network.dto.PlaylistDto
import songbird.apollo.data.network.dto.SongPositionDto

interface PlaylistApi {

    @GET("playlists/")
    suspend fun getPlaylists(): ApiResponse<List<PlaylistDto>>

    @GET("playlist/{playlist_id}")
    suspend fun getPlaylist(
        @Path("playlist_id") playlistId: Int,
    ): ApiResponse<PlaylistDto>

    @POST("playlist/")
    suspend fun newPlaylist(
        @Query("name") name: String,
    ): ApiResponse<PlaylistDto>

    @PATCH("playlist/{playlist_id}")
    suspend fun changePlaylistName(
        @Path("playlist_id") playlistId: Int,
        @Query("name") newName: String,
    ): ApiResponse<EmptyResponse?>

    @DELETE("playlist/{playlist_id}")
    suspend fun deletePlaylist(
        @Path("playlist_id") playlistId: Int,
    ): ApiResponse<EmptyResponse?>

    @POST("playlist/song/")
    suspend fun addSongToPlaylist(
        @Query("playlist_id") playlistId: Int,
        @Query("song_id") songId: Int,
        @Query("position") position: Double
    ): ApiResponse<EmptyResponse?>

    @DELETE("playlist/song/")
    suspend fun removeSongFromPlaylist(
        @Query("playlist_id") playlistId: Int,
        @Query("song_id") songId: Int,
    ): ApiResponse<EmptyResponse?>

    @GET("playlist/{playlist_id}/songs")
    suspend fun getSongs(
        @Path("playlist_id") playlistId: Int,
    ): ApiResponse<List<SongPositionDto>>

    @PUT("playlist/cover/{playlist_id}")
    suspend fun uploadCover(
        @Path("playlist_id") playlistId: Int,
        @Body body: RequestBody
    ): ApiResponse<EmptyResponse?>
}