package com.classroom.agents;

import com.google.adk.agents.Agent;
import com.google.adk.agents.LLMAgent;
import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * LessonPlanAgent - Generates structured lesson plans with long-running operations support
 */
@Agent(name = "LessonPlanAgent", description = "Generates comprehensive lesson plans")
public class LessonPlanAgent extends LLMAgent {
    private static final Logger logger = LoggerFactory.getLogger(LessonPlanAgent.class);
    
    public LessonPlanAgent(AgentRuntime runtime, SessionService sessionService) {
        super(runtime, sessionService);
        logger.info("LessonPlanAgent initialized");
    }
    
    public String generateLessonPlan(String request) {
        logger.info("Generating lesson plan for request: {}", request);
        
        try {
            // Extract subject and grade level from request
            String subject = extractSubject(request);
            String gradeLevel = extractGradeLevel(request);
            String topic = extractTopic(request);
            
            // Use long-running operation for complex lesson plans
            CompletableFuture<String> lessonPlanFuture = CompletableFuture.supplyAsync(() -> {
                return generateDetailedLessonPlan(subject, gradeLevel, topic);
            });
            
            // Simulate long-running operation with timeout
            String lessonPlan = lessonPlanFuture.get(30, TimeUnit.SECONDS);
            
            logger.info("Lesson plan generated successfully");
            return lessonPlan;
            
        } catch (Exception e) {
            logger.error("Error generating lesson plan", e);
            return "Error generating lesson plan. Please provide more specific details about the subject, grade level, and topic.";
        }
    }
    
    private String generateDetailedLessonPlan(String subject, String gradeLevel, String topic) {
        logger.info("Generating detailed lesson plan for {} - {} - {}", subject, gradeLevel, topic);
        
        // Simulate processing time for complex content
        try {
            Thread.sleep(2000); // Simulate long-running operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        StringBuilder lessonPlan = new StringBuilder();
        
        lessonPlan.append("LESSON PLAN\n");
        lessonPlan.append("===========\n\n");
        lessonPlan.append("Subject: ").append(subject).append("\n");
        lessonPlan.append("Grade Level: ").append(gradeLevel).append("\n");
        lessonPlan.append("Topic: ").append(topic).append("\n");
        lessonPlan.append("Duration: 45 minutes\n\n");
        
        lessonPlan.append("LEARNING OBJECTIVES:\n");
        lessonPlan.append("- Students will understand the concept of ").append(topic).append("\n");
        lessonPlan.append("- Students will be able to apply ").append(topic).append(" in practical scenarios\n");
        lessonPlan.append("- Students will demonstrate mastery through exercises\n\n");
        
        lessonPlan.append("LESSON STRUCTURE:\n\n");
        
        lessonPlan.append("1. INTRODUCTION (10 minutes)\n");
        lessonPlan.append("   - Review previous concepts\n");
        lessonPlan.append("   - Introduce ").append(topic).append(" with real-world examples\n");
        lessonPlan.append("   - Engage students with interactive questions\n\n");
        
        lessonPlan.append("2. MAIN CONTENT (25 minutes)\n");
        lessonPlan.append("   - Explain key concepts of ").append(topic).append("\n");
        lessonPlan.append("   - Demonstrate problem-solving techniques\n");
        lessonPlan.append("   - Guided practice with examples\n");
        lessonPlan.append("   - Address common misconceptions\n\n");
        
        lessonPlan.append("3. PRACTICE & ASSESSMENT (8 minutes)\n");
        lessonPlan.append("   - Independent practice problems\n");
        lessonPlan.append("   - Peer collaboration activities\n");
        lessonPlan.append("   - Quick formative assessment\n\n");
        
        lessonPlan.append("4. CLOSURE (2 minutes)\n");
        lessonPlan.append("   - Summarize key learning points\n");
        lessonPlan.append("   - Preview next lesson\n");
        lessonPlan.append("   - Assign homework if applicable\n\n");
        
        lessonPlan.append("MATERIALS NEEDED:\n");
        lessonPlan.append("- Whiteboard/projector\n");
        lessonPlan.append("- Student worksheets\n");
        lessonPlan.append("- Manipulatives (if applicable)\n");
        lessonPlan.append("- Assessment rubric\n\n");
        
        lessonPlan.append("DIFFERENTIATION STRATEGIES:\n");
        lessonPlan.append("- Visual aids for visual learners\n");
        lessonPlan.append("- Hands-on activities for kinesthetic learners\n");
        lessonPlan.append("- Extended challenges for advanced students\n");
        lessonPlan.append("- Additional support for struggling students\n");
        
        return lessonPlan.toString();
    }
    
    private String extractSubject(String request) {
        String lowerRequest = request.toLowerCase();
        if (lowerRequest.contains("math")) return "Mathematics";
        if (lowerRequest.contains("science")) return "Science";
        if (lowerRequest.contains("english")) return "English Language Arts";
        if (lowerRequest.contains("history")) return "History";
        if (lowerRequest.contains("social")) return "Social Studies";
        return "General Subject";
    }
    
    private String extractGradeLevel(String request) {
        String lowerRequest = request.toLowerCase();
        if (lowerRequest.contains("kindergarten") || lowerRequest.contains("k")) return "Kindergarten";
        if (lowerRequest.contains("1st") || lowerRequest.contains("first")) return "1st Grade";
        if (lowerRequest.contains("2nd") || lowerRequest.contains("second")) return "2nd Grade";
        if (lowerRequest.contains("3rd") || lowerRequest.contains("third")) return "3rd Grade";
        if (lowerRequest.contains("4th") || lowerRequest.contains("fourth")) return "4th Grade";
        if (lowerRequest.contains("5th") || lowerRequest.contains("fifth")) return "5th Grade";
        if (lowerRequest.contains("6th") || lowerRequest.contains("sixth")) return "6th Grade";
        if (lowerRequest.contains("middle")) return "Middle School";
        if (lowerRequest.contains("high")) return "High School";
        return "Elementary";
    }
    
    private String extractTopic(String request) {
        String lowerRequest = request.toLowerCase();
        // Math topics
        if (lowerRequest.contains("fraction")) return "Fractions";
        if (lowerRequest.contains("multiplication")) return "Multiplication";
        if (lowerRequest.contains("division")) return "Division";
        if (lowerRequest.contains("geometry")) return "Geometry";
        if (lowerRequest.contains("algebra")) return "Algebra";
        // Science topics
        if (lowerRequest.contains("photosynthesis")) return "Photosynthesis";
        if (lowerRequest.contains("ecosystem")) return "Ecosystems";
        if (lowerRequest.contains("cell")) return "Cell Biology";
        if (lowerRequest.contains("gravity")) return "Gravity and Forces";
        if (lowerRequest.contains("weather")) return "Weather Systems";
        // English topics
        if (lowerRequest.contains("reading")) return "Reading Comprehension";
        if (lowerRequest.contains("writing")) return "Writing Skills";
        return "Core Concepts";
    }
}