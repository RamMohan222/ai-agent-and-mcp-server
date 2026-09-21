package com.ai.mcp.tools;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ai.mcp.model.Result;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

public class AuthenticationToolSpecification {

	private static void log(String log) {
		System.out.println(AuthenticationToolSpecification.class.getSimpleName() + " [LOG] " + log);
	}

	public static McpServerFeatures.SyncToolSpecification create() {
		//@formatter:off
		Map<String, Object> schema = Map.of("type", "object",
					   						"properties", Map.of("username", Map.of("type", "string", "description", "Username"), 
					   											 "password", Map.of("type", "string", "description", "Password")),
					   						"required", List.of("username", "password"));
		
		McpSchema.Tool tool = McpSchema.Tool
				.builder("authentication", schema)
				.description("""
							Authenticates a user using username and password.
							
							IMPORTANT:
							Don't retry for any failures.
							
							Possible outcomes:
							INVALID_USERNAME: Provided username is invalid.
							INVALID_PASSWORD: Provided password is invalid.
							INVALID_CREDENTIALS: Provided username and password are invalid.
							SUCCESS: Returns the body with the accesstoken in JSON format.
						     """).build();

		return McpServerFeatures.SyncToolSpecification
				.builder()
				.tool(tool)
				.callHandler((exchange, request) -> {
					
					String username = (String) request.arguments().get("username");
					String password = (String) request.arguments().get("password");

					log(String.format("u = %s p = %s", username, password /*just to check is it picked right data*/));

					if (username == null || username.isBlank()) {
						log("Invalid username");
						return McpSchema.CallToolResult.builder()
								.structuredContent((Result.of("INVALID_USERNAME")))
								.addTextContent("Invalid username provided")
								.build();
					}

					if (password == null || password.isBlank()) {
						log("Invalid password");
						return McpSchema.CallToolResult.builder()
								.structuredContent(Result.of("INVALID_PASSWORD"))
								.addTextContent("Invalid password provided")
								.build();

					}

					if (username.equals("admin") && password.equals("Welcome@123")) {
						String accesstoken = UUID.randomUUID().toString();
						Map<String, Object> body = Map.of("accessToken", accesstoken, "status", "SUCCESS");
							return McpSchema.CallToolResult.builder()
									.addTextContent(
							                "Authentication successful. " +
							                "accessToken=" + accesstoken
							        )
									.structuredContent(body)
									.build();
					}
					
					log("Invalid credentials");
					return McpSchema.CallToolResult.builder()
							.content(List.of(McpSchema.TextContent
									.builder(Result.of("INVALID_CREDENTIALS"))
									.build()))
							.addTextContent("Invalid credentials provided")
							.build();
				}).build();
		//@formatter:on
	}
}