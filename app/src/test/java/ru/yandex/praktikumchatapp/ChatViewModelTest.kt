import androidx.compose.runtime.collectAsState
import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatState
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

//    @Test
//    fun `send message should update state with MyMessage`() = runTest {
//        val text = "TestMessage"
//        val message = Message.MyMessage(text)
//
//        viewModel.sendMyMessage(text)
//
//        advanceUntilIdle()
//
//        val state = viewModel.chatState.value
//
//        assertTrue(state is ChatState.ResultState)
//
//        val result = state as ChatState.ResultState
//
//        assertTrue(result.messageList.isNotEmpty())
//
//        assertTrue(result.messageList.first() == message)
//    }

    @Test
    fun `send message should update state with MyMessage`() = runTest {
        val text = "TestMessage"
        val message = Message.MyMessage(text)

        viewModel.chatState.test {
            assertEquals(ChatState.InitialState, awaitItem())

            viewModel.sendMyMessage(text)

            val item = awaitItem()

            assertTrue(item is ChatState.ResultState)

            val messageList = (item as ChatState.ResultState).messageList

            assertTrue(messageList.isNotEmpty())

            assertTrue(messageList.first() == message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

        val jobs: MutableList<Job> = mutableListOf<Job>()
        messagesToSend.forEach {
            jobs.add(launch {
                viewModel.sendMyMessage(it.text)
            }
            )
        }
        jobs.joinAll()

        viewModel.chatState.test {
            messagesToSend.forEach {
                val item = awaitItem()

                assertTrue(item is ChatState.ResultState)

                val messageList: List<Message> = (item as ChatState.ResultState).messageList

                assertTrue(messageList.isNotEmpty())

                val lastMessage = messageList.last()

                assertTrue(lastMessage is Message.MyMessage)

                assertEquals((lastMessage as Message.MyMessage).text, it.text)
            }


            cancelAndIgnoreRemainingEvents()
        }
    }
}