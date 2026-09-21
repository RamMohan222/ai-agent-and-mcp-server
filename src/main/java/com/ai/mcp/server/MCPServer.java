package com.ai.mcp.server;

import com.ai.mcp.tools.AccountToolSpecification;
import com.ai.mcp.tools.AuthenticationToolSpecification;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

public class MCPServer {

	public static HttpServletStreamableServerTransportProvider buildTransportProvider(SyncToolSpecification... args) {

		// @formatter:off
		HttpServletStreamableServerTransportProvider transportProvider = HttpServletStreamableServerTransportProvider
				.builder()
				.jsonMapper(McpJsonDefaults.getMapper())
				.mcpEndpoint("/mcp")
				.build();

		// Create a server with custom configuration
		McpSyncServer syncServer = McpServer.sync(transportProvider)
		    .serverInfo("Java-MCP-Server", "1.0.0")
		    .capabilities(ServerCapabilities.builder()
		        .resources(false, true)  // Resource support: subscribe=false, listChanged=true
		        .tools(true)             // Enable tool support with list changes
		        .prompts(true)           // Enable prompt support with list changes
		        .completions()           // Enable completions support
		        .logging()               // Enable logging support
		        .build())
		    .build();
		// @formatter:on

		syncServer.addTool(AuthenticationToolSpecification.create());
		syncServer.addTool(AccountToolSpecification.create());
		for (SyncToolSpecification spec : args) {
			syncServer.addTool(spec);
		}
		return transportProvider;
	}
}
