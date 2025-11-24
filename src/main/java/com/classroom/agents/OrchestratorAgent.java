package com.classroom.agents;

import com.classroom.memory.ClassroomMemoryService;
import com.classroom.observability.MetricsService;
import com.google.adk.agents.Agent;
import com.google.adk.agents.SequentialAgent;
import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import com.google.adk.tools.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Orchestrator Agent - Top-level coordinator for the classroom assistant system
 */
@Agent(name = "OrchestratorAgent", description = "Coordinates classroom assistant operations")
public class OrchestratorAgent extends SequentialAgent {
    private static final Logger logger = LoggerFactory.getLogger(OrchestratorAgent.class);
    
    private final LessonPlanAgent lessonPlanAgent;
    private final WorksheetAgent worksheetAgent;
    private final GradingAgent gradingAgent;
    private final ClassroomMemoryService memoryService;
    private final MetricsService metricsService;
    
    public OrchestratorAgent(AgentRuntime runtime, SessionService sessionService, 
                           ClassroomMemoryService memoryService, ToolRegistry toolRegistry) {
        super(runtime, sessionService);
        this.memoryService = memoryService;
        this.metricsService = MetricsService.getInstance();
        
        // Initialize sub-agents
        this.lessonPlanAgent = new LessonPlanAgent(runtime, sessionService);
        this.worksheetAgent = new WorksheetAgent(runtime, sessionService, toolRegistry);
        this.gradingAgent = new GradingAgent(runtime, sessionService, memoryService);
        
        logger.info("OrchestratorAgent initialized with sub-agents");
    }
    
    public String process(String request) {
        logger.info("Orchestrator processing request");
        
        try {
            // Analyze request type
            RequestType requestType = analyzeRequest(request);
            logger.info("Detected request type: {}", requestType);
            
            switch (requestType) {
                case LESSON_PLAN:
                    return handleLessonPlanRequest(request);
                case WORKSHEET:
                    return handleWorksheetRequest(request);
                case GRADING:
                    return handleGradingRequest(request);
                case PERFORMANCE_ANALYSIS:
                    return handlePerformanceAnalysisRequest(request);
                case MEMORY_SUMMARY:
                    return handleMemorySummaryRequest(request);
                case METRICS_REPORT:
                    return handleMetricsReportRequest(request);
                case FULL_PACKAGE:
                    return handleFullPackageRequest(request);
                default:
                    return "I can help you with lesson plans, worksheets, grading, performance analysis, memory summary or metrics report. Please specify what you need.";
            }
        } catch (Exception e) {
            logger.error("Error in orchestrator processing", e);
            return "I encountered an error processing your request. Please try again.";
        }
    }
    
    private RequestType analyzeRequest(String request) {
        String lowerRequest = request.toLowerCase();
        
        // Check for memory summary requests
        if (lowerRequest.contains("memory") && lowerRequest.contains("summary")) {
            return RequestType.MEMORY_SUMMARY;
        }
        // Check for metrics report requests
        else if (lowerRequest.contains("metrics") && lowerRequest.contains("report")) {
            return RequestType.METRICS_REPORT;
        }
        // Check for performance analysis requests
        else if (lowerRequest.contains("analyze") && (lowerRequest.contains("performance") || lowerRequest.contains("student-scores"))) {
            return RequestType.PERFORMANCE_ANALYSIS;
        }
        // Check for grading requests - be more specific
        else if (lowerRequest.contains("grading") || lowerRequest.contains("feedback") 
            || lowerRequest.contains("grade these") || lowerRequest.contains("grade student")) {
            return RequestType.GRADING;
        } else if ((lowerRequest.contains("worksheet") || lowerRequest.contains("quiz")) && lowerRequest.contains("lesson")) {
            return RequestType.FULL_PACKAGE;
        } else if (lowerRequest.contains("worksheet") || lowerRequest.contains("quiz")) {
            return RequestType.WORKSHEET;
        } else if (lowerRequest.contains("lesson")) {
            return RequestType.LESSON_PLAN;
        }
        
        return RequestType.UNKNOWN; // Default to unknown request
    }
    
    private String handleLessonPlanRequest(String request) {
        logger.info("Handling lesson plan request");
        
        CompletableFuture<String> lessonPlanFuture = CompletableFuture.supplyAsync(() -> {
            return lessonPlanAgent.generateLessonPlan(request);
        });
        
        try {
            String lessonPlan = lessonPlanFuture.get();
            memoryService.storeLessonPlan(lessonPlan);
            metricsService.recordLessonPlanGenerated();
            return lessonPlan;
        } catch (Exception e) {
            logger.error("Error generating lesson plan", e);
            return "Error generating lesson plan. Please try again.";
        }
    }
    
    private String handleWorksheetRequest(String request) {
        logger.info("Handling worksheet request");
        
        String worksheet = worksheetAgent.generateWorksheet(request);
        memoryService.storeWorksheet(worksheet);
        metricsService.recordWorksheetGenerated();
        return worksheet;
    }
    
    private String handleGradingRequest(String request) {
        logger.info("Handling grading request");
        
        long startTime = System.currentTimeMillis();
        String result = gradingAgent.processGrading(request);
        long duration = System.currentTimeMillis() - startTime;
        
        metricsService.recordGradingCompleted(duration);
        return result;
    }
    
    private String handlePerformanceAnalysisRequest(String request) {
        logger.info("Handling performance analysis request");
        
        try {
            // Extract file path from request
            String filePath = extractFilePath(request);
            if (filePath == null) {
                filePath = "sample-data/student-scores.json"; // Default path
            }
            
            // Use StudentPerformanceTool from the worksheet agent's tool registry
            com.classroom.tools.StudentPerformanceTool performanceTool = new com.classroom.tools.StudentPerformanceTool();
            String analysis = performanceTool.analyzePerformance(filePath);
            
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error performing performance analysis", e);
            return "Error analyzing performance data. Please check the file path and format.";
        }
    }
    
    private String handleMemorySummaryRequest(String request) {
        logger.info("Handling memory summary request");
        
        try {
            String summary = memoryService.getMemorySummary();
            return summary;
            
        } catch (Exception e) {
            logger.error("Error retrieving memory summary", e);
            return "Error retrieving memory summary. Please try again.";
        }
    }
    
    private String handleMetricsReportRequest(String request) {
        logger.info("Handling metrics report request");
        
        try {
            String report = metricsService.getMetricsReport();
            return report;
            
        } catch (Exception e) {
            logger.error("Error retrieving metrics report", e);
            return "Error retrieving metrics report. Please try again.";
        }
    }
    
    private String extractFilePath(String request) {
        // Simple extraction - look for file paths in the request
        String[] words = request.split("\\s+");
        for (String word : words) {
            if (word.contains(".json") || word.contains(".csv")) {
                return word;
            }
        }
        return null;
    }
    
    private String handleFullPackageRequest(String request) {
        logger.info("Handling full package request");
        
        StringBuilder result = new StringBuilder();
        
        // Sequential execution
        try {
            // Step 1: Generate lesson plan
            String lessonPlan = lessonPlanAgent.generateLessonPlan(request);
            result.append("LESSON PLAN:\n").append(lessonPlan).append("\n\n");
            
            // Step 2: Generate worksheet based on lesson plan
            String worksheetRequest = "Create worksheet for: " + lessonPlan;
            String worksheet = worksheetAgent.generateWorksheet(worksheetRequest);
            result.append("WORKSHEET:\n").append(worksheet).append("\n\n");
            
            // Store in memory
            memoryService.storeLessonPlan(lessonPlan);
            memoryService.storeWorksheet(worksheet);
            
            result.append("Complete lesson package generated successfully!");
            
        } catch (Exception e) {
            logger.error("Error generating full package", e);
            return "Error generating complete lesson package. Please try again.";
        }
        
        return result.toString();
    }
    
    private enum RequestType {
        LESSON_PLAN,
        WORKSHEET,
        GRADING,
        PERFORMANCE_ANALYSIS,
        MEMORY_SUMMARY,
        METRICS_REPORT,
        FULL_PACKAGE,
        UNKNOWN
    }
}