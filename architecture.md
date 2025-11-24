# Classroom Assistant Architecture

```mermaid
graph TD
    A[Teacher Input] --> B[OrchestratorAgent]
    
    B --> C[LessonPlanAgent]
    C --> D[WorksheetAgent]
    D --> E[GradingAgent]
    
    C --> F[Long-Running Operations]
    D --> G[Google Search Tool]
    D --> H[StudentPerformanceTool]
    E --> I[MemoryBank]
    
    B --> J[SessionService]
    J --> K[InMemorySessionService]
    
    L[Observability Layer] --> M[SLF4J Logging]
    L --> N[Tracing]
    L --> O[Metrics Counter]
    
    B -.-> L
    C -.-> L
    D -.-> L
    E -.-> L
    
    P[A2A Communication] --> B
    P --> C
    P --> D
    P --> E
    
    style B fill:#e1f5fe
    style C fill:#f3e5f5
    style D fill:#e8f5e8
    style E fill:#fff3e0
```

## Agent Flow
1. **OrchestratorAgent** receives teacher request
2. **LessonPlanAgent** generates structured lesson plans (with long-running ops)
3. **WorksheetAgent** creates worksheets using tools (Google Search, StudentPerformanceTool)
4. **GradingAgent** processes student responses and stores in memory
5. Loop back for multiple worksheets if requested

## Tools Integration
- **StudentPerformanceTool**: Custom MCP tool for statistics
- **Google Search**: Built-in tool for content research
- **Code Execution**: JavaProcessTool for calculations

## Memory & Sessions
- **SessionService**: Maintains conversation context
- **MemoryBank**: Long-term storage for student progress
- **Context Compaction**: Automatic memory optimization