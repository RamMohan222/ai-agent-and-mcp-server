package com.ai.mcp.server;

import java.util.EnumSet;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

import com.ai.mcp.tools.AccountToolSpecification;
import com.ai.mcp.tools.AuthenticationToolSpecification;

import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import jakarta.servlet.DispatcherType;

public class HTTPServer {

	public static void main(String[] args) {
		// The MCP server itself does not directly read or intercept network calls.
		// It is a Servlet-based endpoint that receives MCP requests from the
		// MCP client running in the agent, identifies the requested tool,
		// executes the tool, and returns the result to the client.
		HttpServletStreamableServerTransportProvider transportProvider = MCPServer
				.buildTransportProvider(AccountToolSpecification.create(), AuthenticationToolSpecification.create());
		
		int port = 8081;
		Server jettyServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		jettyServer.setHandler(context);
		context.addFilter(CorsFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		context.addServlet(transportProvider, "/mcp/*");
		try {
			jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					jettyServer.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("MCP Server started at http://localhost:" + port + "/mcp");
	}
}
