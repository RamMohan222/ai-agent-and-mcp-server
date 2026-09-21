package com.ai.agent;

import com.ai.mcp.tools.CalculatorTool;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Agent implements AutoCloseable {

	//@formatter:off
	private static OpenAiChatModel openAiChatModel = OpenAiChatModel
			.builder()
			.baseUrl("http://localhost:8080/v1") 	// llama.cpp
			.apiKey("dummy-key-for-local-server") 	// Required field, but ignored by llama.cpp
			.modelName("Qwen3.5-0.8B") 				// Matches your running GGUF
			.temperature(0.0).build();

	private static OllamaChatModel ollamaChatModel = OllamaChatModel
			.builder()
			.baseUrl("http://localhost:11434") // ollama
			// .modelName("llama3.1") // Not able to perform tools chaining
			.modelName("qwen2.5:7b")
			.temperature(0.0).build();

	private static McpTransport transport = StreamableHttpMcpTransport
			.builder()
			.url("http://localhost:8081/mcp") // Local MCPServer provides tools
			.logRequests(true)
			.logResponses(true)
			.build();

	private static McpClient mcpClient = DefaultMcpClient.builder()
			.key("java-mcp-client")
			.transport(transport)
			.build();

	private static McpToolProvider toolProvider = McpToolProvider.builder()
			.mcpClients(mcpClient)
			.build();

	private static Assistant assistant = AiServices.builder(Assistant.class)
			.chatModel(openAiChatModel)
			// .chatModel(ollamaChatModel)
			.tools(new CalculatorTool()) // tightly coupled
			.toolProvider(toolProvider) // loosely coupled with mcp server
			.build();
	//@formatter:on

	public String chat(String prompt) {
		String response = assistant.chat(prompt);
		return response;
	}

	@Override
	public void close() {
		try {
			mcpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}