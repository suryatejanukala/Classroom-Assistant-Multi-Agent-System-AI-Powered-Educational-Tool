package com.classroom.agents;

import com.google.adk.agents.Agent;
import com.google.adk.agents.ToolAgent;
import com.google.adk.core.AgentRuntime;
import com.google.adk.core.SessionService;
import com.google.adk.tools.ToolRegistry;
import com.google.adk.tools.search.GoogleSearchTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * WorksheetAgent - Creates worksheets and quizzes using tools
 */
@Agent(name = "WorksheetAgent", description = "Generates worksheets and quizzes with tool integration")
public class WorksheetAgent extends ToolAgent {
    private static final Logger logger = LoggerFactory.getLogger(WorksheetAgent.class);
    
    private final GoogleSearchTool googleSearchTool;
    private final Random random;
    
    public WorksheetAgent(AgentRuntime runtime, SessionService sessionService, ToolRegistry toolRegistry) {
        super(runtime, sessionService, toolRegistry);
        this.googleSearchTool = new GoogleSearchTool();
        this.random = new Random();
        logger.info("WorksheetAgent initialized with tools");
    }
    
    public String generateWorksheet(String request) {
        logger.info("Generating worksheet for request: {}", request);
        
        try {
            // Extract worksheet parameters
            String subject = extractSubject(request);
            String gradeLevel = extractGradeLevel(request);
            String topic = extractTopic(request);
            
            // Use Google Search for additional content ideas (simulated)
            String searchQuery = subject + " " + topic + " practice problems grade " + gradeLevel;
            String searchResults = performContentSearch(searchQuery);
            
            // Use StudentPerformanceTool to adjust difficulty (simulated)
            String difficultyLevel = determineDifficultyLevel();
            
            // Generate worksheet content
            String worksheet = createWorksheetContent(subject, gradeLevel, topic, difficultyLevel);
            
            logger.info("Worksheet generated successfully");
            return worksheet;
            
        } catch (Exception e) {
            logger.error("Error generating worksheet", e);
            return "Error generating worksheet. Please provide more details about the subject and topic.";
        }
    }
    
    private String performContentSearch(String query) {
        logger.info("Performing content search for: {}", query);
        
        // Simulate Google Search Tool usage
        try {
            // In real implementation, this would use actual Google Search API
            return "Search results for " + query + " - Found relevant educational resources and practice problems";
        } catch (Exception e) {
            logger.warn("Search tool unavailable, using fallback content");
            return "Using built-in content library";
        }
    }
    
    private String determineDifficultyLevel() {
        // Simulate StudentPerformanceTool usage
        // In real implementation, this would analyze actual student performance data
        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        return levels[random.nextInt(levels.length)];
    }
    
    private String createWorksheetContent(String subject, String gradeLevel, String topic, String difficulty) {
        StringBuilder worksheet = new StringBuilder();
        
        worksheet.append("WORKSHEET: ").append(topic).append("\n");
        worksheet.append("=".repeat(30)).append("\n\n");
        worksheet.append("Subject: ").append(subject).append("\n");
        worksheet.append("Grade Level: ").append(gradeLevel).append("\n");
        worksheet.append("Difficulty: ").append(difficulty).append("\n");
        worksheet.append("Date: ___________  Name: ___________________\n\n");
        
        worksheet.append("INSTRUCTIONS:\n");
        worksheet.append("Complete all problems. Show your work where applicable.\n\n");
        
        // Generate problems based on subject and topic
        if (subject.toLowerCase().contains("math")) {
            worksheet.append(generateMathProblems(topic, difficulty));
        } else if (subject.toLowerCase().contains("english")) {
            worksheet.append(generateEnglishProblems(topic, difficulty));
        } else {
            worksheet.append(generateGeneralProblems(topic, difficulty));
        }
        
        worksheet.append("\n\nBONUS QUESTION:\n");
        worksheet.append("Explain how you would use ").append(topic.toLowerCase())
                 .append(" in a real-world situation.\n");
        worksheet.append("_".repeat(50)).append("\n");
        worksheet.append("_".repeat(50)).append("\n");
        
        return worksheet.toString();
    }
    
    private String generateMathProblems(String topic, String difficulty) {
        StringBuilder problems = new StringBuilder();
        
        if (topic.toLowerCase().contains("fraction")) {
            problems.append("FRACTION PROBLEMS:\n\n");
            problems.append("1. Simplify: 8/12 = ___\n\n");
            problems.append("2. Add: 1/4 + 2/4 = ___\n\n");
            problems.append("3. Compare using <, >, or =: 3/5 ___ 2/3\n\n");
            problems.append("4. Word Problem: Sarah ate 2/8 of a pizza and Tom ate 3/8. How much pizza did they eat together?\n\n");
            
            if (difficulty.equals("Advanced")) {
                problems.append("5. Multiply: 2/3 × 3/4 = ___\n\n");
                problems.append("6. Divide: 1/2 ÷ 1/4 = ___\n\n");
            }
        } else if (topic.toLowerCase().contains("multiplication")) {
            problems.append("MULTIPLICATION PROBLEMS:\n\n");
            problems.append("1. 7 × 8 = ___\n\n");
            problems.append("2. 9 × 6 = ___\n\n");
            problems.append("3. 12 × 5 = ___\n\n");
            problems.append("4. Word Problem: There are 8 boxes with 6 apples in each box. How many apples are there in total?\n\n");
            problems.append("5. 15 × 4 = ___\n\n");
            
            if (difficulty.equals("Advanced")) {
                problems.append("6. 23 × 17 = ___\n\n");
                problems.append("7. Word Problem: A classroom has 24 rows of desks with 18 desks in each row. How many desks are there?\n\n");
            }
        } else {
            problems.append("MATH PROBLEMS:\n\n");
            problems.append("1. Solve: 15 + 27 = ___\n\n");
            problems.append("2. Solve: 84 - 39 = ___\n\n");
            problems.append("3. Solve: 6 × 8 = ___\n\n");
            problems.append("4. Word Problem: If there are 24 students and they form groups of 4, how many groups are there?\n\n");
        }
        
        return problems.toString();
    }
    
    private String generateEnglishProblems(String topic, String difficulty) {
        StringBuilder problems = new StringBuilder();
        
        problems.append("ENGLISH LANGUAGE ARTS:\n\n");
        problems.append("1. Circle the nouns in this sentence:\n");
        problems.append("   The happy dog ran quickly through the green park.\n\n");
        
        problems.append("2. Write a sentence using the word 'beautiful':\n");
        problems.append("   _".repeat(40)).append("\n\n");
        
        problems.append("3. Choose the correct verb form:\n");
        problems.append("   Yesterday, I (go/went) to the store.\n\n");
        
        problems.append("4. Reading Comprehension:\n");
        problems.append("   Read the paragraph and answer the question below.\n");
        problems.append("   [Sample paragraph would be inserted here]\n\n");
        
        return problems.toString();
    }
    
    private String generateGeneralProblems(String topic, String difficulty) {
        StringBuilder problems = new StringBuilder();
        
        problems.append("PRACTICE PROBLEMS:\n\n");
        problems.append("1. Define: ").append(topic).append("\n");
        problems.append("   _".repeat(40)).append("\n\n");
        
        problems.append("2. Give an example of ").append(topic.toLowerCase()).append(":\n");
        problems.append("   _".repeat(40)).append("\n\n");
        
        problems.append("3. Explain why ").append(topic.toLowerCase()).append(" is important:\n");
        problems.append("   _".repeat(40)).append("\n");
        problems.append("   _".repeat(40)).append("\n\n");
        
        return problems.toString();
    }
    
    private String extractSubject(String request) {
        String lowerRequest = request.toLowerCase();
        if (lowerRequest.contains("math") || lowerRequest.contains("multiplication") 
            || lowerRequest.contains("fraction") || lowerRequest.contains("division") 
            || lowerRequest.contains("addition") || lowerRequest.contains("subtraction")) {
            return "Mathematics";
        }
        if (lowerRequest.contains("science")) return "Science";
        if (lowerRequest.contains("english")) return "English Language Arts";
        if (lowerRequest.contains("history")) return "History";
        return "General Subject";
    }
    
    private String extractGradeLevel(String request) {
        String lowerRequest = request.toLowerCase();
        if (lowerRequest.contains("5th") || lowerRequest.contains("fifth")) return "5th Grade";
        if (lowerRequest.contains("4th") || lowerRequest.contains("fourth")) return "4th Grade";
        if (lowerRequest.contains("3rd") || lowerRequest.contains("third")) return "3rd Grade";
        return "Elementary";
    }
    
    private String extractTopic(String request) {
        String lowerRequest = request.toLowerCase();
        if (lowerRequest.contains("fraction")) return "Fractions";
        if (lowerRequest.contains("multiplication")) return "Multiplication";
        if (lowerRequest.contains("reading")) return "Reading Comprehension";
        return "Core Concepts";
    }
}