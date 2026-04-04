/*
 * Copyright 2023-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.chat.client

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.StreamingChatModel
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.assertj.core.api.Assertions.assertThat
import reactor.core.publisher.Flux

class ChatClientCoroutineExtensionsTests {

	@Test
	fun callSuspendPrompt() = runBlocking {
		val model = mockk<ChatModel>()
		val prompt = Prompt("hi")
		val response = ChatResponse(listOf())
		every { model.call(prompt) } returns response

		val actual = model.callSuspend(prompt)

		assertThat(actual).isEqualTo(response)
		verify { model.call(prompt) }
	}

	@Test
	fun callSuspendString() = runBlocking {
		val model = mockk<ChatModel>()
		every { model.call("hello") } returns "response"

		val actual = model.callSuspend("hello")

		assertThat(actual).isEqualTo("response")
		verify { model.call("hello") }
	}

	@Test
	fun callSuspendUserMessage() = runBlocking {
		val model = mockk<ChatModel>()
		val message = UserMessage("Hello")
		val chatOptions = ChatOptions.builder().build()
		every { model.call(any<Prompt>()) } returns ChatResponse(listOf())

		model.callSuspend(message, chatOptions)

		verify {
			model.call(withArg { prompt ->
				assertThat(prompt.instructions).containsExactly(message)
				assertThat(prompt.options).isEqualTo(chatOptions)
			})
		}
	}

	@Test
	fun streamAsFlow() = runBlocking {
		val model = mockk<StreamingChatModel>()
		val prompt = Prompt("stream")
		val first = ChatResponse(listOf())
		val second = ChatResponse(listOf())
		every { model.stream(prompt) } returns Flux.just(first, second)

		val values = model.streamAsFlow(prompt).toList()

		assertThat(values).containsExactly(first, second)
		verify { model.stream(prompt) }
	}

}
