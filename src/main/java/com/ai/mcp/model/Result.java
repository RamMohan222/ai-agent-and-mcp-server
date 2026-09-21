package com.ai.mcp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record Result(String status, Object body) {
	static final ObjectMapper mapper = new ObjectMapper();

	public static String of(String status) {
		return of(status, null);
	}

	public static String of(String status, Object body) {
		try {
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Result(status, body));
			System.out.println(json);
			return json;
		} catch (JsonProcessingException e) {
			return new Result(status, body).toString();
		}
	}
}