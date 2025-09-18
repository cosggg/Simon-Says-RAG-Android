package com.darrylbayliss.simonsays.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darrylbayliss.simonsays.ui.theme.SimonSaysTheme
import com.darrylbayliss.simonsays.utils.getTextFromFile
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Welcome

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onNavigateToPlay: () -> Unit,
    onNavigateToInstructions: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Simon Says")
            Spacer(modifier = Modifier.padding(vertical = 16.dp))
            Button(onClick = {
                if (state.isGameReadyToPlay) {
                    onNavigateToPlay()
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Simon Says is setting up.")
                    }
                }
            }) {
                Text("Play")
            }
            Button(onClick = onNavigateToInstructions) {
                Text("Instructions")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setupGame(possibleResponses = context.getTextFromFile("simon_says_responses.txt"))
    }

    LaunchedEffect(state.isGameReadyToPlay) {
        if (state.isGameReadyToPlay) {
            snackbarHostState.showSnackbar("Simon Says is ready to play.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    SimonSaysTheme {
        PlayScreen(hiltViewModel<PlayViewModel>())
    }
}
