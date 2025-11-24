package com.google.adk.agents;

import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import com.google.adk.tools.ToolRegistry;

public class SequentialAgent {
    public SequentialAgent(AgentRuntime runtime, SessionService sessionService) {}
    public SequentialAgent(AgentRuntime runtime, SessionService sessionService, ToolRegistry toolRegistry) {}
    public String process(String input) { return "Sequential Response: " + input; }
}