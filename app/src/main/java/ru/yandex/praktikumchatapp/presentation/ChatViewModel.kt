package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _chatState =
        MutableStateFlow<ChatState>(ChatState.InitialState)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->

                    updateState(
                        currentState = _chatState.value,
                        message = Message.OtherMessage(response)
                    )

                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        updateState(
            currentState = _chatState.value,
            message = Message.MyMessage(messageText)
        )
    }

    private fun updateState(currentState: ChatState, message: Message) {

        val newChatState: ChatState = when (currentState) {
            is ChatState.InitialState ->
                ChatState.ResultState(listOf(message))

            is ChatState.ResultState ->
                ChatState.ResultState(
                    (currentState as ChatState.ResultState).messageList +
                            message
                )

            else -> {
                ChatState.InitialState
            }
        }
        _chatState.update { newChatState }
    }
}