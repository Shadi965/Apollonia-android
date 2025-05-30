package songbird.apollo.data.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import songbird.apollo.data.network.dto.AlbumDto
import songbird.apollo.data.network.dto.ApiResponse

interface AlbumApi {

    @GET("albums/")
    suspend fun getAlbums(): ApiResponse<List<AlbumDto>>

    @GET("album/{album_id}")
    suspend fun getAlbum(
        @Path("album_id") albumId: Int,
    ): ApiResponse<AlbumDto>

    @GET("album/{album_id}/songs")
    suspend fun getSongs(
        @Path("album_id") albumId: Int,
    ): ApiResponse<List<Int>>
}