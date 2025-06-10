package songbird.apollo.presentation.ui.screens.scaffold

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScaffoldViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ScaffoldUiState())
    val uiState: StateFlow<ScaffoldUiState> get() = _uiState

    fun updateUiState(state: ScaffoldUiState) {
        _uiState.value = state
    }
}

data class ScaffoldUiState(
    val showTopBar: Boolean = true,
    val showBottomBar: Boolean = true,
    val showNavigateUp: Boolean = false,
    val showSettingsButton: Boolean = true,
    @StringRes val topBarTitle: Int = android.R.string.untitled
)

val LocalScaffoldViewModel = staticCompositionLocalOf<ScaffoldViewModel?> {
    null
}

@Composable
fun ModifyScaffoldUi(
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    showNavigateUp: Boolean = false,
    showSettingsButton: Boolean = true,
    @StringRes topBarTitle: Int = android.R.string.untitled
) {
    val viewModel = LocalScaffoldViewModel.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                Log.d("Tag", "ON_RESUME triggered")
                viewModel?.updateUiState(
                    ScaffoldUiState(
                        showTopBar = showTopBar,
                        showBottomBar = showBottomBar,
                        showNavigateUp = showNavigateUp,
                        showSettingsButton = showSettingsButton,
                        topBarTitle = topBarTitle
                    )
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}