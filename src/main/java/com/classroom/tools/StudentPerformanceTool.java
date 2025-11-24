package com.classroom.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.adk.tools.Tool;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom MCP Tool for analyzing student performance data
 */
@Tool(name = "StudentPerformanceTool", description = "Analyzes student performance from JSON/CSV data")
public class StudentPerformanceTool {
    private static final Logger logger = LoggerFactory.getLogger(StudentPerformanceTool.class);
    
    private final ObjectMapper objectMapper;
    
    public StudentPerformanceTool() {
        this.objectMapper = new ObjectMapper();
        logger.info("StudentPerformanceTool initialized");
    }
    
    public String analyzePerformance(String filePath) {
        logger.info("Analyzing student performance from file: {}", filePath);
        
        try {
            List<Double> scores;
            
            if (filePath.toLowerCase().endsWith(".json")) {
                scores = parseJsonFile(filePath);
            } else if (filePath.toLowerCase().endsWith(".csv")) {
                scores = parseCsvFile(filePath);
            } else {
                return "Unsupported file format. Please provide JSON or CSV file.";
            }
            
            if (scores.isEmpty()) {
                return "No valid scores found in the file.";
            }
            
            return generateStatistics(scores);
            
        } catch (Exception e) {
            logger.error("Error analyzing performance data", e);
            return "Error analyzing performance data: " + e.getMessage();
        }
    }
    
    public String analyzePerformanceFromData(String jsonData) {
        logger.info("Analyzing student performance from provided data");
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            List<Double> scores = new ArrayList<>();
            
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    if (node.has("score")) {
                        scores.add(node.get("score").asDouble());
                    }
                }
            } else if (rootNode.has("students")) {
                JsonNode studentsNode = rootNode.get("students");
                for (JsonNode student : studentsNode) {
                    if (student.has("score")) {
                        scores.add(student.get("score").asDouble());
                    }
                }
            }
            
            return generateStatistics(scores);
            
        } catch (Exception e) {
            logger.error("Error parsing performance data", e);
            return "Error parsing performance data: " + e.getMessage();
        }
    }
    
    private List<Double> parseJsonFile(String filePath) throws IOException {
        List<Double> scores = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(new FileReader(filePath));
        
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                if (node.has("score")) {
                    scores.add(node.get("score").asDouble());
                } else if (node.isNumber()) {
                    scores.add(node.asDouble());
                }
            }
        }
        
        return scores;
    }
    
    private List<Double> parseCsvFile(String filePath) throws IOException, CsvValidationException {
        List<Double> scores = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            boolean isFirstLine = true;
            
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                
                // Assume score is in the last column
                if (nextLine.length > 0) {
                    try {
                        double score = Double.parseDouble(nextLine[nextLine.length - 1]);
                        scores.add(score);
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid score format: {}", nextLine[nextLine.length - 1]);
                    }
                }
            }
        }
        
        return scores;
    }
    
    private String generateStatistics(List<Double> scores) {
        Collections.sort(scores);
        
        double mean = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double median = calculateMedian(scores);
        double min = scores.get(0);
        double max = scores.get(scores.size() - 1);
        
        // Grade distribution
        int aGrades = (int) scores.stream().filter(s -> s >= 90).count();
        int bGrades = (int) scores.stream().filter(s -> s >= 80 && s < 90).count();
        int cGrades = (int) scores.stream().filter(s -> s >= 70 && s < 80).count();
        int dGrades = (int) scores.stream().filter(s -> s >= 60 && s < 70).count();
        int fGrades = (int) scores.stream().filter(s -> s < 60).count();
        
        StringBuilder stats = new StringBuilder();
        stats.append("STUDENT PERFORMANCE ANALYSIS\n");
        stats.append("============================\n\n");
        stats.append("Total Students: ").append(scores.size()).append("\n");
        stats.append("Mean Score: ").append(String.format("%.2f", mean)).append("\n");
        stats.append("Median Score: ").append(String.format("%.2f", median)).append("\n");
        stats.append("Minimum Score: ").append(String.format("%.2f", min)).append("\n");
        stats.append("Maximum Score: ").append(String.format("%.2f", max)).append("\n\n");
        
        stats.append("GRADE DISTRIBUTION:\n");
        stats.append("A (90-100): ").append(aGrades).append(" students (")
             .append(String.format("%.1f", (aGrades * 100.0 / scores.size()))).append("%)\n");
        stats.append("B (80-89):  ").append(bGrades).append(" students (")
             .append(String.format("%.1f", (bGrades * 100.0 / scores.size()))).append("%)\n");
        stats.append("C (70-79):  ").append(cGrades).append(" students (")
             .append(String.format("%.1f", (cGrades * 100.0 / scores.size()))).append("%)\n");
        stats.append("D (60-69):  ").append(dGrades).append(" students (")
             .append(String.format("%.1f", (dGrades * 100.0 / scores.size()))).append("%)\n");
        stats.append("F (0-59):   ").append(fGrades).append(" students (")
             .append(String.format("%.1f", (fGrades * 100.0 / scores.size()))).append("%)\n\n");
        
        // Recommendations
        stats.append("RECOMMENDATIONS:\n");
        if (mean < 70) {
            stats.append("- Class average is below 70%. Consider reviewing core concepts.\n");
        }
        if (fGrades > scores.size() * 0.2) {
            stats.append("- More than 20% of students are failing. Additional support needed.\n");
        }
        if (aGrades > scores.size() * 0.3) {
            stats.append("- Strong performance! Consider advanced challenges for top students.\n");
        }
        
        return stats.toString();
    }
    
    private double calculateMedian(List<Double> sortedScores) {
        int size = sortedScores.size();
        if (size % 2 == 0) {
            return (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
        } else {
            return sortedScores.get(size / 2);
        }
    }
}