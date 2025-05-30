package songbird.apollo.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import songbird.apollo.presentation.ui.screens.test.AlbumListScreen
import songbird.apollo.presentation.ui.theme.ApolloniaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApolloniaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlbumListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}