package songbird.apollo.data.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import songbird.apollo.data.network.dto.AlbumDto
import songbird.apollo.data.network.dto.ApiResponse
import songbird.apollo.data.network.requests.AlbumsIdsRequest

interface AlbumApi {

    @GET("albums/")
    suspend fun getAlbums(): ApiResponse<List<AlbumDto>>

    @GET("album/{album_id}")
    suspend fun getAlbum(
        @Path("album_id") albumId: Int,
    ): ApiResponse<AlbumDto>

    @POST("albums/list")
    suspend fun getAlbums(
        @Body albumsIds: AlbumsIdsRequest
    ): ApiResponse<List<AlbumDto>>

    @GET("album/{album_id}/songs")
    suspend fun getSongs(
        @Path("album_id") albumId: Int,
    ): ApiResponse<List<Int>>
}