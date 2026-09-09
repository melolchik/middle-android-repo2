package ru.yandex.praktikumchatapp.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.pow

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    val initialDelay = 1000L

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
//            .catch { throwable ->
//                Log.d("chat", "catch error = $throwable")
//
//            }
            .retryWhen { cause, attempt ->
                if(cause is Exception) {
                    val delayTime : Long = initialDelay * 2.0.pow(attempt.toDouble()).toLong()
                    delay(delayTime)
                    return@retryWhen true
                }

                return@retryWhen false
            }
            // TODO Задание 2: добавьте обработку ошибок
    }
}