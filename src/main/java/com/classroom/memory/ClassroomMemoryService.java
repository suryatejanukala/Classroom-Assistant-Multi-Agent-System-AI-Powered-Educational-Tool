package com.classroom.memory;

import com.google.adk.memory.MemoryBank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Memory service for storing classroom-related data with context compaction
 */
public class ClassroomMemoryService {
    private static final Logger logger = LoggerFactory.getLogger(ClassroomMemoryService.class);
    private static final int MAX_MEMORY_ENTRIES = 100;
    
    private final MemoryBank memoryBank;
    private final Map<String, Object> sessionData;
    private final DateTimeFormatter formatter;
    
    public ClassroomMemoryService(MemoryBank memoryBank) {
        this.memoryBank = memoryBank;
        this.sessionData = new HashMap<>();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.info("ClassroomMemoryService initialized");
    }
    
    public void storeLessonPlan(String lessonPlan) {
        logger.info("Storing lesson plan in memory");
        
        String timestamp = LocalDateTime.now().format(formatter);
        String key = "lesson_plan_" + timestamp.replace(" ", "_").replace(":", "-");
        
        memoryBank.store(key, lessonPlan);
        sessionData.put("last_lesson_plan", lessonPlan);
        
        checkMemoryLimits();
    }
    
    public void storeWorksheet(String worksheet) {
        logger.info("Storing worksheet in memory");
        
        String timestamp = LocalDateTime.now().format(formatter);
        String key = "worksheet_" + timestamp.replace(" ", "_").replace(":", "-");
        
        memoryBank.store(key, worksheet);
        sessionData.put("last_worksheet", worksheet);
        
        checkMemoryLimits();
    }
    
    public void storeGradingResults(List<?> gradingResults) {
        logger.info("Storing grading results in memory");
        
        String timestamp = LocalDateTime.now().format(formatter);
        String key = "grading_results_" + timestamp.replace(" ", "_").replace(":", "-");
        
        memoryBank.store(key, gradingResults);
        sessionData.put("last_grading_results", gradingResults);
        
        checkMemoryLimits();
    }
    
    public void storeStudentProgress(String studentName, Map<String, Object> progressData) {
        logger.info("Storing student progress for: {}", studentName);
        
        String key = "student_progress_" + studentName.toLowerCase().replace(" ", "_");
        
        // Retrieve existing progress or create new
        @SuppressWarnings("unchecked")
        Map<String, Object> existingProgress = (Map<String, Object>) memoryBank.retrieve(key);
        if (existingProgress == null) {
            existingProgress = new HashMap<>();
        }
        
        // Update with new data
        existingProgress.putAll(progressData);
        existingProgress.put("last_updated", LocalDateTime.now().format(formatter));
        
        memoryBank.store(key, existingProgress);
        checkMemoryLimits();
    }
    
    public String getLastLessonPlan() {
        return (String) sessionData.get("last_lesson_plan");
    }
    
    public String getLastWorksheet() {
        return (String) sessionData.get("last_worksheet");
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getLastGradingResults() {
        return (List<Object>) sessionData.get("last_grading_results");
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getStudentProgress(String studentName) {
        String key = "student_progress_" + studentName.toLowerCase().replace(" ", "_");
        return (Map<String, Object>) memoryBank.retrieve(key);
    }
    
    public List<String> getAllStoredLessonPlans() {
        List<String> lessonPlans = new ArrayList<>();
        
        // Retrieve all lesson plan keys
        for (String key : memoryBank.getAllKeys()) {
            if (key.startsWith("lesson_plan_")) {
                String lessonPlan = (String) memoryBank.retrieve(key);
                if (lessonPlan != null) {
                    lessonPlans.add(lessonPlan);
                }
            }
        }
        
        return lessonPlans;
    }
    
    public String getMemorySummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append("MEMORY SUMMARY\n");
        summary.append("==============\n\n");
        
        int lessonPlanCount = 0;
        int worksheetCount = 0;
        int gradingResultCount = 0;
        int studentProgressCount = 0;
        
        for (String key : memoryBank.getAllKeys()) {
            if (key.startsWith("lesson_plan_")) {
                lessonPlanCount++;
            } else if (key.startsWith("worksheet_")) {
                worksheetCount++;
            } else if (key.startsWith("grading_results_")) {
                gradingResultCount++;
            } else if (key.startsWith("student_progress_")) {
                studentProgressCount++;
            }
        }
        
        summary.append("Stored Lesson Plans: ").append(lessonPlanCount).append("\n");
        summary.append("Stored Worksheets: ").append(worksheetCount).append("\n");
        summary.append("Stored Grading Results: ").append(gradingResultCount).append("\n");
        summary.append("Student Progress Records: ").append(studentProgressCount).append("\n");
        summary.append("Total Memory Entries: ").append(memoryBank.getAllKeys().size()).append("\n");
        
        return summary.toString();
    }
    
    private void checkMemoryLimits() {
        int currentSize = memoryBank.getAllKeys().size();
        
        if (currentSize > MAX_MEMORY_ENTRIES) {
            logger.info("Memory limit exceeded, performing context compaction");
            performContextCompaction();
        }
    }
    
    private void performContextCompaction() {
        logger.info("Performing context compaction to optimize memory usage");
        
        List<String> keysToRemove = new ArrayList<>();
        
        // Remove oldest entries, keeping the most recent ones
        List<String> allKeys = new ArrayList<>(memoryBank.getAllKeys());
        allKeys.sort(String::compareTo); // Sort by timestamp in key
        
        // Remove oldest 20% of entries
        int entriesToRemove = (int) (allKeys.size() * 0.2);
        for (int i = 0; i < entriesToRemove && i < allKeys.size(); i++) {
            keysToRemove.add(allKeys.get(i));
        }
        
        // Remove selected entries
        for (String key : keysToRemove) {
            memoryBank.remove(key);
        }
        
        logger.info("Context compaction completed. Removed {} entries", keysToRemove.size());
    }
    
    public void clearMemory() {
        logger.info("Clearing all memory data");
        memoryBank.clear();
        sessionData.clear();
    }
    
    public void exportMemoryData() {
        logger.info("Exporting memory data for backup");
        
        // In a real implementation, this would export to a file
        String summary = getMemorySummary();
        logger.info("Memory export summary:\n{}", summary);
    }
}