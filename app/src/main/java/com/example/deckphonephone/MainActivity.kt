package com.example.deckphonephone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deckphonephone.deck.ui.DeckScreen
import com.example.deckphonephone.deck.ui.DeckViewModel
import com.example.deckphonephone.ui.theme.DeckphonephoneTheme

class MainActivity : ComponentActivity() {
    private val appContainer by lazy {
        DeckAppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeckphonephoneTheme {
                val viewModel = viewModel<DeckViewModel>(
                    factory = DeckViewModel.Factory(appContainer.useCases),
                )
                DeckScreen(viewModel = viewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeckPreview() {
    DeckphonephoneTheme {}
}
