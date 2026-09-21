package com.ai.mcp.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class CalculatorTool {

	@Tool("Adds two numbers")
	public int add(@P("first number") int a, 
			@P("second number") int b) {
		System.out.println("Tool called +");
		return a + b;
	}

	@Tool("Multiplies two numbers")
	public int multiply(@P("first number") int a, 
			@P("second number") int b) {
		System.out.println("Tool called +");
		return a * b;
	}
}