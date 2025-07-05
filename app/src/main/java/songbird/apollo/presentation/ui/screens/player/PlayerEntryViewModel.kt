package songbird.apollo.presentation.ui.screens.player

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import songbird.apollo.presentation.model.SongPreviewUi
import javax.inject.Inject

val LocalPlayerEntryViewModel = staticCompositionLocalOf<PlayerEntryViewModel> {
    error("No PlayerStateViewModel provided")
}

@HiltViewModel
class PlayerEntryViewModel @Inject constructor() : ViewModel() {

    private val _playerVisible = MutableStateFlow(PlayerVisibility())
    val playerVisible: StateFlow<PlayerVisibility> get() = _playerVisible

    private val _playbackData = MutableStateFlow(PlaybackData())
    val playbackData: StateFlow<PlaybackData> get() = _playbackData

    fun playerRegister(playlist: List<SongPreviewUi>, startIndex: Int = 0) {
        if(!_playerVisible.value.isVisible)
            _playerVisible.update { it.copy(isVisible = true) }
        _playbackData.update { it.copy(playlist = playlist, startIndex = startIndex) }
    }

    fun hidePlayer(isHidden: Boolean = true) = _playerVisible.update { it.copy(isHidden = isHidden) }
    fun closePlayer() = _playerVisible.update { it.copy(isVisible = false) }
    fun expandPlayer() = _playerVisible.update { it.copy(isExpanded = true) }
    fun collapsePlayer() = _playerVisible.update { it.copy(isExpanded = false) }

    fun setBottomPadding(padding: Dp) = _playerVisible.update { it.copy(bottomPadding = padding) }
    fun setCollapseHeight(height: Dp) = _playerVisible.update { it.copy(collapseHeight = height) }
}

data class PlayerVisibility(
    val isVisible: Boolean = false,
    val isHidden: Boolean = false,
    val isExpanded: Boolean = false,
    val collapseHeight: Dp = 64.dp,
    val bottomPadding: Dp = 80.dp
)

data class PlaybackData(
    val playlist: List<SongPreviewUi> = emptyList(),
    val startIndex: Int = 0
)