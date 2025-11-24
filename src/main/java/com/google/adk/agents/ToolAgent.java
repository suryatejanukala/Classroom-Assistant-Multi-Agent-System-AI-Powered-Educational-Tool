package com.google.adk.agents;

import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import com.google.adk.tools.ToolRegistry;

public class ToolAgent {
    public ToolAgent(AgentRuntime runtime, SessionService sessionService, ToolRegistry toolRegistry) {}
    public String process(String input) { return "Tool Response: " + input; }
}