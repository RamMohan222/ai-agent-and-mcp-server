package com.ai.mcp.tools;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ai.mcp.model.Result;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

public class AccountToolSpecification {

	private static void log(String log) {
		System.out.println(AccountToolSpecification.class.getSimpleName() + " [LOG] " + log);
	}
	
	private static boolean isValidToken(String accessToken) {
		try {
			UUID.fromString(accessToken);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static McpServerFeatures.SyncToolSpecification create() {
		//@formatter:off
		Map<String, Object> schema = Map.of("type", "object",
					   						"properties", Map.of("username", Map.of("type", "string", "description", "Valid username"),
					   											 "accesstoken", Map.of("type", "string", "description", "Access token from authenticaion response")),
					   						"required", List.of("username", "accesstoken"));
		
		McpSchema.Tool tool = McpSchema.Tool
				.builder("get_user_details", schema)
				.description("""
						Fetches user details.

					    This tool requires a valid access token.
					
					    IMPORTANT:
					    1. The accesstoken argument MUST be the accessToken returned
					    by the authentication tool.
					    2. Don't retry for any failures.
					
					    Workflow:
					    1. Call authentication with username and password.
					    2. If authentication returns SUCCESS, extract accessToken.
					    3. Pass that exact accessToken to this tool.
					    4. Never invent an access token.
					    
					    Possible outcomes:

						INVALID_USERNAME: Provided username is invalid.
						INVALID_ACCESSTOKEN: Provided accesstoken is invalid.
						SUCCESS: Returns the user details in JSON format.
						
						""").build();

		return McpServerFeatures.SyncToolSpecification
				.builder()
				.tool(tool)
				.callHandler((exchange, request) -> {
					
					String username = (String) request.arguments().get("username");
					String accesstoken = (String) request.arguments().get("accesstoken");

					log(String.format("u = %s a = %s", username, accesstoken));

					if (username == null || username.isBlank()) {
						log("Invalid username");
						return McpSchema.CallToolResult.builder()
								.addContent(McpSchema.TextContent.builder(Result.of("INVALID_USERNAME")).build()).build();
					}

					if (accesstoken == null || accesstoken.isBlank() || !isValidToken(accesstoken)) {
						log("Invalid accesstoken");
						return McpSchema.CallToolResult.builder()
								.addContent(McpSchema.TextContent.builder(Result.of("INVALID_ACCESSTOKEN")).build()).build();

					}

				    Map<String, Object> body = Map.of("id", accesstoken, 
				    		"firstName", "Ram",
				    		"lastName", "Mohan",
				    		"username", username,
				    		"avator", "https://example.com/1/ram.png"
				    		);
		
					return McpSchema.CallToolResult.builder()
									.content(List.of(McpSchema.TextContent.builder(Result.of("SUCCESS", body)).build()))
									.build();
				}).build();
		//@formatter:on
	}
}
