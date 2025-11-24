package com.classroom.agents;

import com.classroom.memory.ClassroomMemoryService;
import com.google.adk.agents.Agent;
import com.google.adk.agents.LLMAgent;
import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * GradingAgent - Processes student responses and provides feedback
 */
@Agent(name = "GradingAgent", description = "Grades student work and provides feedback")
public class GradingAgent extends LLMAgent {
    private static final Logger logger = LoggerFactory.getLogger(GradingAgent.class);
    
    private final ClassroomMemoryService memoryService;
    
    public GradingAgent(AgentRuntime runtime, SessionService sessionService, 
                       ClassroomMemoryService memoryService) {
        super(runtime, sessionService);
        this.memoryService = memoryService;
        logger.info("GradingAgent initialized with memory service");
    }
    
    public String processGrading(String request) {
        logger.info("Processing grading request");
        
        try {
            // Parse student responses from request
            List<StudentResponse> responses = parseStudentResponses(request);
            
            // Grade each response
            List<GradingResult> results = new ArrayList<>();
            for (StudentResponse response : responses) {
                GradingResult result = gradeResponse(response);
                results.add(result);
            }
            
            // Generate summary
            String summary = generateGradingSummary(results);
            
            // Store results in memory
            memoryService.storeGradingResults(results);
            
            logger.info("Grading completed for {} responses", responses.size());
            return summary;
            
        } catch (Exception e) {
            logger.error("Error processing grading", e);
            return "Error processing grading. Please check the format of student responses.";
        }
    }
    
    private List<StudentResponse> parseStudentResponses(String request) {
        List<StudentResponse> responses = new ArrayList<>();
        
        // Simple parsing - in real implementation would be more sophisticated
        String[] lines = request.split("\n");
        String currentStudent = null;
        StringBuilder currentAnswer = new StringBuilder();
        
        for (String line : lines) {
            if (line.startsWith("Student:")) {
                if (currentStudent != null) {
                    responses.add(new StudentResponse(currentStudent, currentAnswer.toString().trim()));
                }
                currentStudent = line.substring(8).trim();
                currentAnswer = new StringBuilder();
            } else if (line.startsWith("Answer:")) {
                currentAnswer.append(line.substring(7).trim());
            } else if (!line.trim().isEmpty() && currentStudent != null) {
                currentAnswer.append(" ").append(line.trim());
            }
        }
        
        if (currentStudent != null) {
            responses.add(new StudentResponse(currentStudent, currentAnswer.toString().trim()));
        }
        
        return responses;
    }
    
    private GradingResult gradeResponse(StudentResponse response) {
        logger.debug("Grading response for student: {}", response.getStudentName());
        
        // Analyze the response
        int score = analyzeResponse(response.getAnswer());
        String feedback = generateFeedback(response.getAnswer(), score);
        
        return new GradingResult(response.getStudentName(), response.getAnswer(), score, feedback);
    }
    
    private int analyzeResponse(String answer) {
        // Simple scoring logic - in real implementation would use more sophisticated NLP
        if (answer == null || answer.trim().isEmpty()) {
            return 0;
        }
        
        String lowerAnswer = answer.toLowerCase();
        int score = 50; // Base score
        
        // Check for key indicators of good answers
        if (lowerAnswer.contains("because") || lowerAnswer.contains("therefore")) {
            score += 20; // Shows reasoning
        }
        if (lowerAnswer.length() > 50) {
            score += 15; // Detailed response
        }
        if (lowerAnswer.contains("example") || lowerAnswer.contains("for instance")) {
            score += 15; // Provides examples
        }
        
        return Math.min(100, score);
    }
    
    private String generateFeedback(String answer, int score) {
        StringBuilder feedback = new StringBuilder();
        
        if (score >= 90) {
            feedback.append("Excellent work! ");
        } else if (score >= 80) {
            feedback.append("Good job! ");
        } else if (score >= 70) {
            feedback.append("Nice effort! ");
        } else {
            feedback.append("Keep working on this. ");
        }
        
        // Specific feedback based on answer analysis
        if (answer.length() < 20) {
            feedback.append("Try to provide more detailed explanations. ");
        }
        if (!answer.toLowerCase().contains("because")) {
            feedback.append("Consider explaining your reasoning. ");
        }
        
        feedback.append("Score: ").append(score).append("/100");
        
        return feedback.toString();
    }
    
    private String generateGradingSummary(List<GradingResult> results) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("GRADING SUMMARY\n");
        summary.append("===============\n\n");
        
        // Individual results
        for (GradingResult result : results) {
            summary.append("Student: ").append(result.getStudentName()).append("\n");
            summary.append("Score: ").append(result.getScore()).append("/100\n");
            summary.append("Feedback: ").append(result.getFeedback()).append("\n\n");
        }
        
        // Class statistics
        double averageScore = results.stream()
                .mapToInt(GradingResult::getScore)
                .average()
                .orElse(0.0);
        
        summary.append("CLASS STATISTICS:\n");
        summary.append("Average Score: ").append(String.format("%.1f", averageScore)).append("/100\n");
        summary.append("Total Students: ").append(results.size()).append("\n");
        
        long highScores = results.stream().filter(r -> r.getScore() >= 80).count();
        summary.append("Students with 80+ scores: ").append(highScores).append("\n");
        
        return summary.toString();
    }
    
    // Inner classes for data structures
    private static class StudentResponse {
        private final String studentName;
        private final String answer;
        
        public StudentResponse(String studentName, String answer) {
            this.studentName = studentName;
            this.answer = answer;
        }
        
        public String getStudentName() { return studentName; }
        public String getAnswer() { return answer; }
    }
    
    private static class GradingResult {
        private final String studentName;
        private final String answer;
        private final int score;
        private final String feedback;
        
        public GradingResult(String studentName, String answer, int score, String feedback) {
            this.studentName = studentName;
            this.answer = answer;
            this.score = score;
            this.feedback = feedback;
        }
        
        public String getStudentName() { return studentName; }
        public String getAnswer() { return answer; }
        public int getScore() { return score; }
        public String getFeedback() { return feedback; }
    }
}