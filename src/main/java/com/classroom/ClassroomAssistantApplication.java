package com.classroom;

import com.classroom.agents.OrchestratorAgent;
import com.classroom.memory.ClassroomMemoryService;
import com.classroom.observability.MetricsService;
import com.classroom.tools.StudentPerformanceTool;
import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import com.google.adk.core.InMemorySessionService;
import com.google.adk.memory.MemoryBank;
import com.google.adk.tools.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Classroom Assistant Multi-Agent System
 */
public class ClassroomAssistantApplication {
    private static final Logger logger = LoggerFactory.getLogger(ClassroomAssistantApplication.class);
    
    private final AgentRuntime runtime;
    private final SessionService sessionService;
    private final MemoryBank memoryBank;
    private final OrchestratorAgent orchestrator;
    private final MetricsService metricsService;
    
    public ClassroomAssistantApplication() {
        logger.info("Initializing Classroom Assistant Multi-Agent System");
        
        // Initialize core services
        this.sessionService = new InMemorySessionService();
        this.memoryBank = new MemoryBank();
        this.runtime = new AgentRuntime();
        
        // Initialize memory service
        ClassroomMemoryService memoryService = new ClassroomMemoryService(memoryBank);
        
        // Register tools
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new StudentPerformanceTool());
        
        // Initialize metrics service
        this.metricsService = MetricsService.getInstance();
        
        // Initialize orchestrator agent
        this.orchestrator = new OrchestratorAgent(
            runtime, 
            sessionService, 
            memoryService, 
            toolRegistry
        );
        
        logger.info("Classroom Assistant initialized successfully");
    }
    
    public String processRequest(String teacherRequest) {
        logger.info("Processing teacher request: {}", teacherRequest);
        long startTime = System.currentTimeMillis();
        
        try {
            metricsService.setGauge("active_sessions", 1);
            String response = orchestrator.process(teacherRequest);
            
            long duration = System.currentTimeMillis() - startTime;
            metricsService.recordTimer("request_duration", duration);
            
            logger.info("Request processed successfully in {}ms", duration);
            return response;
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return "I apologize, but I encountered an error processing your request. Please try again.";
        } finally {
            metricsService.setGauge("active_sessions", 0);
        }
    }
    
    public static void main(String[] args) {
        ClassroomAssistantApplication app = new ClassroomAssistantApplication();
        
        // Try different requests:
        // "Create a math lesson plan for 5th grade fractions" - Lesson Plan
        // "Generate a worksheet for multiplication practice" - Worksheet
        // "Create a science lesson about photosynthesis for middle school" - Different subject for Lesson plan
        //  "Grade these student responses:\n\n" +
        //                "Student: John\n" +
        //                "Answer: A fraction is a part of a whole number. For example, 1/2 means one part out of two equal parts.\n\n" +
        //                "Student: Sarah\n" +
        //                "Answer: Fractions represent division. 3/4 means 3 divided by 4, which equals 0.75.\n\n" +
        //                "Student: Mike\n" +
        //                "Answer: A fraction has a numerator on top and denominator on bottom. The denominator shows how many parts the whole is divided into." - Grading
        // "Analyze student performance data from sample-data/student-scores.json" - Performance Analysis
        // "Show memory summary" - Memory Summary
        // "Show metrics report" - Metrics Report

        String request = "Create a math lesson plan for 5th grade fractions";
        String response = app.processRequest(request);
        System.out.println("Response: " + response);
    }
}