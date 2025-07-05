package songbird.apollo.presentation.ui.screens

import kotlinx.serialization.Serializable
import songbird.apollo.presentation.model.SongPreviewUi

@Serializable
sealed interface AppRoute

@Serializable
sealed interface AppGraph

@Serializable
data object FavoriteGraph : AppGraph {
    @Serializable
    data object FavoriteScreenRoute : AppRoute
}

@Serializable
data object LibraryGraph : AppGraph {
    @Serializable
    data object LibraryScreenRoute : AppRoute
}

@Serializable
data object SearchGraph : AppGraph {
    @Serializable
    data object SearchScreenRoute : AppRoute
}

@Serializable
data object SettingsScreenRoute : AppRoute

@Serializable
data class SongMenuRoute private constructor(
    private val songId: Int,
    private val title: String,
    private val artist: String,
    private val albumId: Int,
    private val duration: Int,
    private val position: Double,
    private val coverUrl: String?,
    private val isDownloaded: Boolean,
) : AppRoute {
    constructor(song: SongPreviewUi) : this(
        songId = song.id,
        title = song.title,
        artist = song.artist,
        albumId = song.albumId,
        duration = song.duration,
        position = song.position,
        coverUrl = song.coverUrl,
        isDownloaded = song.isDownloaded
    )

    val song: SongPreviewUi
        get() {
            return SongPreviewUi(
                id = songId,
                title = title,
                artist = artist,
                albumId = albumId,
                duration = duration,
                position = position,
                coverUrl = coverUrl,
                isDownloaded = isDownloaded
            )
        }
}
