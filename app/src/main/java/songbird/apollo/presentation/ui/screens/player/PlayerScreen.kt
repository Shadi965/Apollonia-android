package songbird.apollo.presentation.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import songbird.apollo.presentation.model.SongUi
import songbird.apollo.presentation.ui.LoadResult
import songbird.apollo.presentation.ui.LoadResult.Error
import songbird.apollo.presentation.ui.LoadResult.Loading
import songbird.apollo.presentation.ui.LoadResult.Success
import songbird.apollo.presentation.ui.screens.player.PlayerViewModel.Factory
import songbird.apollo.presentation.ui.screens.scaffold.ModifyScaffoldUi

@Composable
fun PlayerScreen(
    songId: Int
) {
    // TODO: Экран воспроизведения должен быть поверх scaffold
    ModifyScaffoldUi(
        showTopBar = false,
        showBottomBar = false
    )

    val viewModel = hiltViewModel<PlayerViewModel, Factory> { factory ->
        factory.create(songId)
    }
    val isPlaying by viewModel.isPlaying.collectAsState()
    val songResult: LoadResult<SongUi> by viewModel.song.collectAsState()

    when (songResult) {
        is Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        is Success -> {
            val song = (songResult as Success).data

            LaunchedEffect(song.id) {
                viewModel.preparePlayer()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                song.coverUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        // TODO: Описание
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    song.coverUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = song.title, fontSize = 24.sp, color = Color.White)
                    Text(text = song.artist, fontSize = 18.sp, color = Color.LightGray)

                    Spacer(modifier = Modifier.height(36.dp))

                    IconButton(onClick = { viewModel.playPause() }) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }


        is Error -> {
            val error = songResult as Error
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = error.message ?: "Error",
                    color = Color.Red,
                    fontSize = 18.sp
                )
            }
        }

        is LoadResult.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Song not found", color = Color.Gray)
            }
        }
    }
}