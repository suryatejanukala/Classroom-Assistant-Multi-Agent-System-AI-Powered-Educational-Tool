# Classroom Assistant Design Document

## Purpose
A multi-agent system that assists teachers with lesson planning, worksheet creation, grading, and student performance tracking using ADK-Java framework.

## Agent Descriptions

### 1. OrchestratorAgent
- **Role**: Top-level coordinator
- **Responsibilities**: 
  - Receives teacher requests
  - Orchestrates sequential agent execution
  - Manages loops for multiple worksheet generation
- **Pattern**: SequentialAgent

### 2. LessonPlanAgent
- **Role**: Lesson plan generator
- **Responsibilities**:
  - Creates structured lesson plans
  - Handles large content with long-running operations
- **Pattern**: LLMAgent with async operations

### 3. WorksheetAgent
- **Role**: Worksheet and quiz creator
- **Responsibilities**:
  - Generates worksheets and quizzes
  - Uses Google Search for content research
  - Integrates StudentPerformanceTool for data analysis
- **Pattern**: ToolAgent wrapper

### 4. GradingAgent
- **Role**: Assessment processor
- **Responsibilities**:
  - Summarizes student answers
  - Generates personalized feedback
  - Stores results in MemoryBank
- **Pattern**: LLMAgent with memory integration

## Tool Descriptions

### Custom MCP Tool: StudentPerformanceTool
- **Input**: JSON/CSV file with student marks
- **Output**: Statistical analysis (mean, median, grade distribution)
- **Usage**: Informs worksheet difficulty adjustment

### Built-in Tools
- **Google Search**: Content research for worksheets
- **JavaProcessTool**: Mathematical calculations and data processing

## Sequence Diagrams

### Main Workflow
```
Teacher -> OrchestratorAgent: "Create math lesson with worksheet"
OrchestratorAgent -> LessonPlanAgent: Generate lesson plan
LessonPlanAgent -> OrchestratorAgent: Structured lesson plan
OrchestratorAgent -> WorksheetAgent: Create worksheet
WorksheetAgent -> GoogleSearch: Research problems
WorksheetAgent -> StudentPerformanceTool: Get class statistics
WorksheetAgent -> OrchestratorAgent: Generated worksheet
OrchestratorAgent -> Teacher: Complete lesson package
```

### Grading Workflow
```
Teacher -> OrchestratorAgent: "Grade these student responses"
OrchestratorAgent -> GradingAgent: Process responses
GradingAgent -> MemoryBank: Store student progress
GradingAgent -> OrchestratorAgent: Feedback summary
OrchestratorAgent -> Teacher: Grading results
```

## Memory Usage

### SessionService (InMemorySessionService)
- Maintains conversation context
- Stores temporary agent states
- Enables context continuity across agent calls

### MemoryBank
- Long-term storage for:
  - Student performance history
  - Generated lesson plans
  - Worksheet templates
- Automatic context compaction when memory exceeds limits

## Logging & Observability

### SLF4J Logging
- Agent lifecycle events
- Tool execution results
- Error conditions and recovery

### Tracing
- Request flow through agent chain
- Performance bottlenecks identification
- Tool execution timing

### Metrics
- Counter: `quizzes_generated`
- Counter: `lessons_created`
- Timer: `grading_duration`
- Gauge: `active_sessions`

## Error Handling

### Agent Failures
- Retry mechanism for transient failures
- Fallback to simplified responses
- Graceful degradation of features

### Tool Failures
- Alternative tool selection
- Manual fallback options
- User notification of limitations

### Memory Issues
- Automatic context compaction
- Session cleanup policies
- Memory usage monitoring

## A2A Communication
- Agents communicate through structured message passing
- Event-driven notifications for status updates
- Shared context through SessionService
- Asynchronous processing for long-running operations