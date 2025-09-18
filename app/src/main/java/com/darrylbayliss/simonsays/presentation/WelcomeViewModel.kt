package com.darrylbayliss.simonsays.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darrylbayliss.simonsays.domain.CreateSimonSaysGame
import com.darrylbayliss.simonsays.domain.IsSimonSaysReadyToPlay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelcomeViewState(
    val isGameReadyToPlay: Boolean = false
)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val createSimonSaysGame: CreateSimonSaysGame,
    private val isSimonSaysReadyToPlay: IsSimonSaysReadyToPlay
) : ViewModel() {

    private val _uiState: MutableStateFlow<WelcomeViewState> = MutableStateFlow(WelcomeViewState())

    val uiState: StateFlow<WelcomeViewState> = _uiState

    fun setupGame(possibleResponses: List<String>) {
        viewModelScope.launch {
            val gameReady = createSimonSaysGame(possibleResponses = possibleResponses)

            _uiState.update {
                it.copy(
                    isGameReadyToPlay = gameReady
                )
            }
        }
    }

    fun isGameReadyToPlay() {
        viewModelScope.launch {
            val readyToPlay = isSimonSaysReadyToPlay()

            _uiState.update {
                it.copy(
                    isGameReadyToPlay = readyToPlay
                )
            }
        }
    }
}