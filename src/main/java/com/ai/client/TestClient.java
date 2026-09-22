package com.ai.client;

import com.ai.agent.Agent;

public class TestClient {

	public static void main(String[] args) {
		try (Agent agent = new Agent()) {
			String response = agent.chat("Get details of admin with password Welcome@123");
			System.out.println(response);
			
//	        System.out.println("--------");
//	        String response = agent.chat("Login with Ram");
//	        System.out.println(response);
//	        System.out.println("--------");
//	        String response = agent.chat("Login with Ram and hello@1");
//	        System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
