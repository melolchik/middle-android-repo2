package ru.yandex.praktikumchatapp.presentation

sealed class ChatState {
    object InitialState : ChatState()
    data class ResultState(val messageList: List<Message>) : ChatState()
    //shouldShowKeyboard нет необходимости добавлять, т.к. появляется один раз при смене состояния
    //а если это MyMessage, то клавиатура уже поднята
}