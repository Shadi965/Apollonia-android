package songbird.apollo.presentation.ui.screens.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import songbird.apollo.presentation.model.SongPreviewUi
import songbird.apollo.presentation.ui.screens.LocalNavController
import songbird.apollo.presentation.ui.screens.SongMenuRoute
import java.util.Locale

fun formatMillis(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

@Composable
private fun PlaylistPanel(
    visible: Boolean,
    songs: List<SongPreviewUi>,
    order: List<Int>,
    currentIndex: Int,
    isShuffled: Boolean,
    onSongClick: (Int) -> Unit,
    onShuffleToggle: () -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onClose() }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Очередь воспроизведения",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onShuffleToggle
                            ) {
                                Icon(
                                    Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = if (isShuffled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(onClick = onClose) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    val listState = rememberLazyListState()

                    LaunchedEffect(visible, currentIndex) {
                        if (visible && currentIndex in order.indices) {
                            delay(300)
                            listState.animateScrollToItem(
                                index = currentIndex,
                                scrollOffset = -200
                            )
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(order) { position, songIndex ->
                            val song = songs.getOrNull(songIndex)
                            if (song != null) {
                                PlaylistItem(
                                    song = song,
                                    isCurrentSong = position == currentIndex,
                                    onClick = { onSongClick(position) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistItem(
    song: SongPreviewUi,
    isCurrentSong: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentSong)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentSong) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        if (isCurrentSong) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Card(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = "Album cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrentSong)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentSong)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isCurrentSong) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Currently playing",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier
) {
    val stateViewModel = LocalPlayerEntryViewModel.current
    val viewModel: PlayerViewModel = hiltViewModel()

    val playbackData by stateViewModel.playbackData.collectAsState()

    LaunchedEffect(playbackData) {
        viewModel.setPlaylist(playbackData.playlist, playbackData.startIndex)
    }

    val visibleState by stateViewModel.playerVisible.collectAsState()
    if (visibleState.isHidden) return
    if (visibleState.isExpanded) {
        FullScreenPlayer(
            onCollapse = { stateViewModel.collapsePlayer() },
            modifier = modifier.fillMaxSize()
        )
    } else {
        MiniPlayer(
            onExpand = { stateViewModel.expandPlayer() },
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = visibleState.bottomPadding)
                .height(visibleState.collapseHeight)
        )
    }
}

@Composable
private fun MiniPlayer(
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlayerViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()
    val song by viewModel.currentSong.collectAsState()

    Card(
        modifier = modifier
            .clickable { onExpand() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = "Album cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { viewModel.playToggle() }) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FullScreenPlayer(
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlayerViewModel = hiltViewModel()
    val navController = LocalNavController.current

    val state by viewModel.state.collectAsState()
    val progress by viewModel.pbPosition.collectAsState()
    val song by viewModel.currentSong.collectAsState()
    val lyrics by viewModel.lyrics.collectAsState()

    val playlist by viewModel.playlist.collectAsState()
    val index = state.index
    val isPlaying = state.isPlaying
    val isShuffled = state.isShuffled
    val duration = song.duration.toLong() * 1000L

    var showPlaylist by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }

    LaunchedEffect(song.id) {
        if (song.id != 0) {
            viewModel.loadLyrics()
        }
    }

    BackHandler(onBack = {
        when {
            showPlaylist -> showPlaylist = false
            showLyrics -> showLyrics = false
            else -> onCollapse()
        }
    })

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* Поглощаем клики */ }
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 25.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .size(280.dp)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = "Album cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = {
                    navController.navigate(SongMenuRoute(song))
                }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column {
                CustomSlider(
                    value = if (duration > 0) progress.toFloat() / duration.toFloat() else 0f,
                    onValueChange = { viewModel.seekTo((it * duration).toLong()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatMillis(progress),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatMillis(duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previous() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                FilledIconButton(
                    onClick = { viewModel.playToggle() },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { viewModel.next() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        showLyrics = !showLyrics
                        if (showLyrics) showPlaylist = false
                    }
                ) {
                    Icon(
                        Icons.Default.TextFields,
                        contentDescription = "Show lyrics",
                        tint = if (showLyrics) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        showPlaylist = !showPlaylist
                        if (showPlaylist) showLyrics = false
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Show playlist",
                        tint = if (showPlaylist) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    PlaylistPanel(
        visible = showPlaylist,
        songs = state.songs,
        order = state.order,
        currentIndex = index,
        isShuffled = isShuffled,
        onSongClick = { songIndex ->
            viewModel.skipTo(songIndex)
            showPlaylist = false
        },
        onShuffleToggle = { viewModel.shuffle() },
        onClose = { showPlaylist = false }
    )

    LyricsPanel(
        visible = showLyrics,
        lyrics = lyrics,
        currentPosition = progress,
        onClose = { showLyrics = false },
        onClick = { viewModel.seekTo(it.toLong()) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        activeTickColor = MaterialTheme.colorScheme.primary,
        inactiveTickColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = sliderColors,
        thumb = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .shadow(2.dp, CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
            )
        },
        track = { sliderState ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sliderState.value)
                        .height(4.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    )
}

