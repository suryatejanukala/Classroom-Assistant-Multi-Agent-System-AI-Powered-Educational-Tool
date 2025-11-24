# Classroom Assistant Multi-Agent System

A comprehensive multi-agent system built with ADK-Java that assists teachers with lesson planning, worksheet creation, grading, and student performance tracking.

## Features

- **Multi-Agent Architecture**: Orchestrated system with specialized agents
- **Lesson Plan Generation**: Automated creation of structured lesson plans
- **Worksheet Creation**: Dynamic worksheet and quiz generation with tool integration
- **Intelligent Grading**: Automated grading with personalized feedback
- **Performance Analytics**: Student performance analysis and statistics
- **Memory Management**: Long-term storage with context compaction
- **Observability**: Comprehensive logging, tracing, and metrics

## Architecture

### Agents

1. **OrchestratorAgent**: Top-level coordinator managing agent workflow
2. **LessonPlanAgent**: Generates structured lesson plans with long-running operations
3. **WorksheetAgent**: Creates worksheets using Google Search and performance tools
4. **GradingAgent**: Processes student responses and stores results in memory

### Tools

- **StudentPerformanceTool**: Custom MCP tool for analyzing student performance data
- **Google Search Tool**: Built-in tool for content research
- **Java Process Tool**: Code execution for calculations

### Memory & Sessions

- **InMemorySessionService**: Maintains conversation context
- **MemoryBank**: Long-term storage for student progress and lesson data
- **Context Compaction**: Automatic memory optimization

## Project Setup

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- ADK-Java SDK

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd classroom-assistant
```

2. Build the project:
```bash
mvn clean compile
```

3. Run the application:
```bash
mvn exec:java -Dexec.mainClass="com.classroom.ClassroomAssistantApplication"
```

## Usage

### Basic Usage

```java
ClassroomAssistantApplication app = new ClassroomAssistantApplication();

// Generate a complete lesson package
String request = "Create a math lesson plan for 5th grade fractions with a worksheet";
String response = app.processRequest(request);
System.out.println(response);
```

### Request Types

#### Lesson Plan Generation
```
"Create a lesson plan for 4th grade multiplication"
"Generate a science lesson about photosynthesis for middle school"
```

#### Worksheet Creation
```
"Create a worksheet for fractions practice"
"Generate a quiz on American history for 8th grade"
```

#### Grading
```
"Grade these student responses:
Student: John
Answer: Fractions are parts of a whole number...

Student: Sarah  
Answer: A fraction represents..."
```

#### Full Package
```
"Create a complete math lesson for 5th grade fractions with worksheet and examples"
```

## Input/Output Examples

### Input: Lesson Plan Request
```
"Create a math lesson plan for 5th grade fractions"
```

### Output: Structured Lesson Plan
```
LESSON PLAN
===========

Subject: Mathematics
Grade Level: 5th Grade
Topic: Fractions
Duration: 45 minutes

LEARNING OBJECTIVES:
- Students will understand the concept of Fractions
- Students will be able to apply Fractions in practical scenarios
- Students will demonstrate mastery through exercises

LESSON STRUCTURE:

1. INTRODUCTION (10 minutes)
   - Review previous concepts
   - Introduce Fractions with real-world examples
   - Engage students with interactive questions

2. MAIN CONTENT (25 minutes)
   - Explain key concepts of Fractions
   - Demonstrate problem-solving techniques
   - Guided practice with examples
   - Address common misconceptions

[... continued ...]
```

### Input: Worksheet Request
```
"Create a worksheet for 5th grade fractions"
```

### Output: Generated Worksheet
```
WORKSHEET: Fractions
==============================

Subject: Mathematics
Grade Level: 5th Grade
Difficulty: Intermediate
Date: ___________  Name: ___________________

INSTRUCTIONS:
Complete all problems. Show your work where applicable.

FRACTION PROBLEMS:

1. Simplify: 8/12 = ___

2. Add: 1/4 + 2/4 = ___

3. Compare using <, >, or =: 3/5 ___ 2/3

4. Word Problem: Sarah ate 2/8 of a pizza and Tom ate 3/8. 
   How much pizza did they eat together?

[... continued ...]
```

## Conversation Flows

### Complete Lesson Creation Flow
```
Teacher: "I need a complete math lesson for 5th grade fractions"
System: [OrchestratorAgent] → [LessonPlanAgent] → [WorksheetAgent]
Output: Complete lesson package with plan and worksheet
```

### Grading Flow
```
Teacher: "Grade these student fraction problems"
System: [OrchestratorAgent] → [GradingAgent] → [MemoryBank storage]
Output: Individual grades, feedback, and class statistics
```

### Performance Analysis Flow
```
Teacher: "Analyze my class performance data"
System: [WorksheetAgent] → [StudentPerformanceTool]
Output: Statistical analysis and recommendations
```

## Configuration

### Logging Configuration
Modify `src/main/resources/logback.xml` to adjust logging levels and output formats.

### Memory Settings
Adjust memory limits in `ClassroomMemoryService.java`:
```java
private static final int MAX_MEMORY_ENTRIES = 100;
```

### Tool Configuration
Register additional tools in `ClassroomAssistantApplication.java`:
```java
ToolRegistry toolRegistry = new ToolRegistry();
toolRegistry.register(new StudentPerformanceTool());
toolRegistry.register(new CustomTool());
```

## Observability

### Metrics
The system tracks:
- `lessons_created`: Number of lesson plans generated
- `quizzes_generated`: Number of worksheets created
- `grading_sessions`: Number of grading operations
- `grading_duration`: Time taken for grading operations
- `active_sessions`: Current active user sessions

### Logging
- Agent lifecycle events
- Tool execution results
- Memory operations
- Error conditions and recovery

### Tracing
- Request flow through agent chain
- Performance bottlenecks
- Tool execution timing

## Development

### Adding New Agents
1. Extend appropriate ADK-Java agent class
2. Implement required methods
3. Register with OrchestratorAgent
4. Add logging and metrics

### Adding New Tools
1. Implement Tool interface
2. Add @Tool annotation
3. Register in ToolRegistry
4. Update documentation

### Memory Extensions
1. Extend ClassroomMemoryService
2. Add new storage methods
3. Update context compaction logic
4. Test memory limits

## Troubleshooting

### Common Issues

**Memory Overflow**
- Check memory limits in ClassroomMemoryService
- Verify context compaction is working
- Monitor memory usage logs

**Tool Failures**
- Verify tool registration
- Check network connectivity for external tools
- Review tool-specific logs

**Agent Communication Issues**
- Check SessionService configuration
- Verify agent initialization order
- Review A2A communication logs

### Debug Mode
Enable debug logging:
```xml
<logger name="com.classroom" level="DEBUG"/>
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Support

For issues and questions:
- Check the troubleshooting section
- Review logs in `logs/` directory
- Create an issue in the repository