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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.StreamingChatModel
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.core.ParameterizedTypeReference

/**
 * Extensions for [ChatClient] providing a reified generic adapters for `entity` and `responseEntity`
 *
 * @author Josh Long
 */

inline fun <reified T : Any> ChatClient.CallResponseSpec.entity(): T =
	entity(object : ParameterizedTypeReference<T>() {}) as T

inline fun <reified T : Any> ChatClient.CallResponseSpec.responseEntity(): ResponseEntity<ChatResponse, T> =
	responseEntity(object : ParameterizedTypeReference<T>() {})

/**
 * Coroutine-friendly adapter over [StreamingChatModel.stream] returning a [Flow] of [ChatResponse].
 */
fun StreamingChatModel.streamAsFlow(prompt: Prompt): Flow<ChatResponse> = this.stream(prompt).asFlow()

/**
 * Coroutine-friendly adapter over [ChatModel.call].
 */
suspend fun ChatModel.callSuspend(prompt: Prompt): ChatResponse = this.call(prompt)

/**
 * Coroutine-friendly adapter over [ChatModel.call] using a String input.
 */
suspend fun ChatModel.callSuspend(message: String): String? = this.call(message)

/**
 * Coroutine-friendly adapter over [ChatModel.call] using one user message.
 */
suspend fun ChatModel.callSuspend(message: UserMessage, chatOptions: ChatOptions? = null): ChatResponse =
	this.call(Prompt(listOf(message), chatOptions))
