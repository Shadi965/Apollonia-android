package songbird.apollo.presentation.ui.screens.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.ui.screens.LocalNavController

@Composable
fun SongActions(
    song: SongPreviewUi
) {
    val viewModel = hiltViewModel<SongActionsViewModel, SongActionsViewModel.Factory> { factory ->
        factory.create(song)
    }
    val navController = LocalNavController.current

    val state by viewModel.state.collectAsState()
    // TODO: Ошибки не обрабатываются
    SongActionsSheet(
        songTitle = state.title,
        songArtist = state.artist,
        albumName = state.albumName,
        coverUrl = state.coverUrl,
        isFavorite = state.isFavorite,
        onDismiss = { navController.popBackStack() },
        toFavorite = { viewModel.toggleFavorite() },
        onPlay = { /* TODO: передать на воспроизведение */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongActionsSheet(
    songTitle: String,
    songArtist: String,
    albumName: String,
    coverUrl: String?,
    isFavorite: Boolean = false,
    onDismiss: () -> Unit,
    onPlay: () -> Unit = {},
    toFavorite: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    onDownload: () -> Unit = {}
) = ModalBottomSheet(onDismiss) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPlay() }
            ) {
                AsyncImage(
                    model = coverUrl,
                    // TODO: Описание
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = songTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = songArtist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = albumName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // TODO: Добавить в ресурсы
        SongMenuItem(
            icon = if (isFavorite) Icons.Default.HeartBroken else Icons.Default.Favorite,
            text = if (isFavorite) "Remove from favorites" else "Add to favorites",
            onClick = toFavorite
        )
        SongMenuItem(
            icon = Icons.Default.Add,
            text = "Add to playlist",
            onClick = onAddToPlaylist,
            enabled = false
        )
        SongMenuItem(
            icon = Icons.Default.Download,
            text = "Download",
            onClick = onDownload,
            enabled = false
        )
    }
}

@Composable
private fun SongMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val contentAlpha = if (enabled) 1f else 0.4f
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
    val iconTint = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (enabled) it.clickable(onClick = onClick)
                else it
            }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}
