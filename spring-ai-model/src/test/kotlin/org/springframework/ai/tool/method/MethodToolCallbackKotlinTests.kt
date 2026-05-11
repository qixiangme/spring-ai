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

package org.springframework.ai.tool.method

import kotlinx.coroutines.delay
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.tool.annotation.Tool

class MethodToolCallbackKotlinTests {

	@Test
	fun `suspending tool method excludes continuation parameter from schema`() {
		val toolCallback = toolCallback()

		assertThat(toolCallback.toolDefinition.inputSchema())
			.contains("\"city\"")
			.doesNotContain("continuation")
	}

	@Test
	fun `suspending tool method can be invoked as a tool callback`() {
		val toolCallback = toolCallback()

		val result = toolCallback.call("""{"city":"Seoul"}""")

		assertThat(result).isEqualTo("\"Weather in Seoul is sunny\"")
	}

	private fun toolCallback() =
		MethodToolCallbackProvider.builder()
			.toolObjects(WeatherTools())
			.build()
			.toolCallbacks
			.single()

	class WeatherTools {

		@Tool(description = "Get the weather conditions for a city")
		suspend fun weather(city: String): String {
			delay(1)
			return "Weather in $city is sunny"
		}

	}

}
