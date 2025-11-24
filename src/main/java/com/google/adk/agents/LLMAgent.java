package com.google.adk.agents;

import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;

public class LLMAgent {
    public LLMAgent(AgentRuntime runtime, SessionService sessionService) {}
    public String process(String input) { return "LLM Response: " + input; }
}