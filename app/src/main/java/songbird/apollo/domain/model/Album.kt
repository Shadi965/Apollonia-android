package songbird.apollo.domain.model

data class Album(
    val id: Int,
    val title: String,
    val artist: String,

    val trackCount: Int,
    val discCount: Int,

    val date: String,
    val copyright: String,
    val genre: String,
)
