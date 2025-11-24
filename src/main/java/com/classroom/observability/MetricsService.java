package com.classroom.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Metrics service for tracking system performance and usage
 */
public class MetricsService {
    private static final Logger metricsLogger = LoggerFactory.getLogger("metrics");
    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
    
    private static MetricsService instance;
    
    private final ConcurrentHashMap<String, AtomicLong> counters;
    private final ConcurrentHashMap<String, AtomicLong> timers;
    private final ConcurrentHashMap<String, AtomicLong> gauges;
    
    private MetricsService() {
        this.counters = new ConcurrentHashMap<>();
        this.timers = new ConcurrentHashMap<>();
        this.gauges = new ConcurrentHashMap<>();
        logger.info("MetricsService initialized");
    }
    
    public static synchronized MetricsService getInstance() {
        if (instance == null) {
            instance = new MetricsService();
        }
        return instance;
    }
    
    public void incrementCounter(String name) {
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).incrementAndGet();
        metricsLogger.info("COUNTER {} incremented to {}", name, counters.get(name).get());
    }
    
    public void recordTimer(String name, long durationMs) {
        timers.computeIfAbsent(name, k -> new AtomicLong(0)).set(durationMs);
        metricsLogger.info("TIMER {} recorded: {}ms", name, durationMs);
    }
    
    public void setGauge(String name, long value) {
        gauges.computeIfAbsent(name, k -> new AtomicLong(0)).set(value);
        metricsLogger.info("GAUGE {} set to {}", name, value);
    }
    
    public void recordLessonPlanGenerated() {
        incrementCounter("lessons_created");
    }
    
    public void recordWorksheetGenerated() {
        incrementCounter("quizzes_generated");
    }
    
    public void recordGradingCompleted(long durationMs) {
        incrementCounter("grading_sessions");
        recordTimer("grading_duration", durationMs);
    }
    
    public String getMetricsReport() {
        StringBuilder report = new StringBuilder();
        report.append("METRICS REPORT\n==============\n\n");
        
        report.append("COUNTERS:\n");
        counters.forEach((name, value) -> 
            report.append("  ").append(name).append(": ").append(value.get()).append("\n"));
        
        report.append("\nTIMERS (ms):\n");
        timers.forEach((name, value) -> 
            report.append("  ").append(name).append(": ").append(value.get()).append("\n"));
        
        return report.toString();
    }
}