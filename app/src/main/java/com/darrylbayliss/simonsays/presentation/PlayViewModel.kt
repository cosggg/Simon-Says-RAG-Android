package com.darrylbayliss.simonsays.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darrylbayliss.simonsays.domain.Message
import com.darrylbayliss.simonsays.domain.SendMessageToSimon
import com.darrylbayliss.simonsays.domain.StartSimonSays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val startSimonSays: StartSimonSays,
    private val sendMessageToSimon: SendMessageToSimon,
) : ViewModel() {
    val messages: StateFlow<List<Message>>
        get() = _messages

    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(
        emptyList()
    )

    private val awaitingMessageFromSimon = Message(
        text = "...",
        isFromMe = false
    )

    fun startGame() {
        viewModelScope.launch {
            _messages.update { messages ->
                val mutableList = messages.toMutableList()
                mutableList += awaitingMessageFromSimon
                mutableList
            }

            val message = startSimonSays()

            _messages.update { messages ->
                val mutableList = messages.toMutableList()
                mutableList.removeAt(mutableList.lastIndex)
                mutableList += message
                mutableList
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val newMessage = Message(
                text = text,
                isFromMe = true
            )

            _messages.update { messages ->
                val mutableList = messages.toMutableList()
                mutableList += listOf(newMessage, awaitingMessageFromSimon)
                mutableList
            }

            val message =
                sendMessageToSimon(
                    message = Message(
                        text = text,
                        isFromMe = true
                    )
                )

            _messages.update { messages ->
                val mutableList = messages.toMutableList()
                mutableList.removeAt(mutableList.lastIndex)
                mutableList += message
                mutableList
            }
        }
    }
}
