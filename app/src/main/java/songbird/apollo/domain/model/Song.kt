package songbird.apollo.domain.model

data class Song(
    val id: Int,

    val title: String,
    val artist: String,
    val composer: String,

    val albumId: Int,
    val track: Int,
    val disc: Int,

    val date: String,
    val copyright: String,
    val genre: String,
    val duration: Int,

    val coverUrl: String?,
)

data class SongPreview(
    val id: Int,
    val title: String,
    val artist: String,
    val albumId: Int,
    val duration: Int,

    val position: Double,
    val coverUrl: String?,
)
