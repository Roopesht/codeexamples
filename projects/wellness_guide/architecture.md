# Architecture: Wellness Guide

## 1. Application Flow

```mermaid
graph TD
    Start([User Opens App]) --> ModeSelect{Select Mode}
    ModeSelect -->|Configure| ConfigMode[Configure Mode]
    ModeSelect -->|Practice| PracticeMode[Practice Mode]
    
    ConfigMode --> EnterGoal[Enter Wellness Goal]
    EnterGoal --> GetRecs[Get LLM Recommendations]
    GetRecs --> SelectYoga[Select Yogasanas]
    SelectYoga --> SetDuration[Set Durations]
    SetDuration --> SaveRoutine[Save to localStorage]
    SaveRoutine --> Done1([Routine Saved])
    
    PracticeMode --> LoadRoutine[Load Saved Routine]
    LoadRoutine --> DisplayYoga[Display Yogasana]
    DisplayYoga --> StartTimer[Start Timer]
    StartTimer --> TimerDone{Timer = 0?}
    TimerDone -->|No| StartTimer
    TimerDone -->|Yes| MoreYoga{More Yogasanas?}
    MoreYoga -->|Yes| DisplayYoga
    MoreYoga -->|No| Complete([Routine Complete])
    
    classDef configStyle fill:#e1f5ff,stroke:#333,stroke-width:2px
    classDef practiceStyle fill:#fff4e1,stroke:#333,stroke-width:2px
    classDef successStyle fill:#d4edda,stroke:#333,stroke-width:2px
    
    class ConfigMode,EnterGoal,GetRecs,SelectYoga,SetDuration configStyle
    class PracticeMode,LoadRoutine,DisplayYoga,StartTimer practiceStyle
    class SaveRoutine,Complete successStyle
```

## 2. Component Structure

```mermaid
graph TD
    App["App Component<br/>Manages mode state<br/>Loads master data"]
    
    App --> ModeSelector["ModeSelector<br/>Configure / Practice buttons"]
    
    App --> ConfigFlow["Configure Flow"]
    App --> PracticeFlow["Practice Flow"]
    
    ConfigFlow --> GoalInput["GoalInput<br/>Text input + button"]
    ConfigFlow --> RecList["RecommendationList<br/>Display suggestions"]
    ConfigFlow --> RoutineForm["RoutineForm<br/>Select + set durations"]
    
    PracticeFlow --> PracticeSession["PracticeSession<br/>Display current yogasana"]
    PracticeFlow --> Timer["Timer<br/>Countdown + auto-advance"]
    
    GoalInput --> YogaCard["YogasanaCard<br/>shared component"]
    RecList --> YogaCard
    RoutineForm --> YogaCard
    PracticeSession --> YogaCard
    
    classDef appStyle fill:#4a90e2,color:#fff,stroke:#333,stroke-width:3px
    classDef configStyle fill:#e1f5ff,stroke:#333,stroke-width:2px
    classDef practiceStyle fill:#fff4e1,stroke:#333,stroke-width:2px
    classDef sharedStyle fill:#f0f0f0,stroke:#333,stroke-width:2px
    
    class App appStyle
    class ConfigFlow,GoalInput,RecList,RoutineForm configStyle
    class PracticeFlow,PracticeSession,Timer practiceStyle
    class YogaCard sharedStyle
```

## 3. Data Flow - Configure Mode

```mermaid
sequenceDiagram
    participant User
    participant GoalInput
    participant llmService
    participant GeminiAPI
    participant RecList
    participant RoutineForm
    participant storageService
    participant localStorage
    
    User->>GoalInput: Enter wellness goal
    User->>GoalInput: Click "Get Recommendations"
    GoalInput->>llmService: getRecommendations(goal)
    llmService->>GeminiAPI: POST request
    GeminiAPI-->>llmService: Yogasana names
    llmService-->>RecList: Display recommendations
    
    User->>RecList: Select yogasanas
    User->>RecList: Enter durations
    User->>RoutineForm: Click "Save Routine"
    RoutineForm->>storageService: saveRoutine(routine)
    storageService->>localStorage: Store JSON
    storageService-->>RoutineForm: Success
    RoutineForm-->>User: Show success message
```

## 4. Data Flow - Practice Mode

```mermaid
sequenceDiagram
    participant User
    participant PracticeSession
    participant storageService
    participant localStorage
    participant yogasanaService
    participant yogasanasJSON
    participant Timer
    
    User->>PracticeSession: Open Practice Mode
    PracticeSession->>storageService: loadRoutine()
    storageService->>localStorage: Get "wellness_routine"
    localStorage-->>storageService: Routine object
    
    PracticeSession->>yogasanaService: getYogasanaById(id)
    yogasanaService->>yogasanasJSON: Read master data
    yogasanasJSON-->>yogasanaService: Yogasana details
    yogasanaService-->>PracticeSession: Display yogasana
    
    PracticeSession->>Timer: Start timer (duration)
    Timer-->>Timer: Count down every 1s
    Timer->>PracticeSession: onComplete()
    PracticeSession->>PracticeSession: Next yogasana
```

## 5. Service Layer Architecture

```mermaid
graph TB
    subgraph Presentation[Presentation Layer]
        Components[React Components<br/>GoalInput, RecList, Timer, etc.]
    end
    
    subgraph Service[Service Layer]
        llmService[llmService.js<br/>• getRecommendations]
        storageService[storageService.js<br/>• saveRoutine<br/>• loadRoutine]
        yogasanaService[yogasanaService.js<br/>• loadMasterData<br/>• getYogasanaById]
    end
    
    subgraph Data[Data Layer]
        localStorage[(localStorage<br/>wellness_routine)]
        yogasanaJSON[(yogasanas.json<br/>Master Data)]
    end
    
    Components --> llmService
    Components --> storageService
    Components --> yogasanaService
    
    llmService -.->|HTTP| GeminiAPI[Gemini API]
    storageService --> localStorage
    yogasanaService --> yogasanaJSON
    
    style Presentation fill:#e1f5ff
    style Service fill:#fff9e1
    style Data fill:#e8f5e9
```

## 6. State Management

```mermaid
graph TD
    subgraph AppState[App Component State]
        mode["mode: 'configure' | 'practice'"]
        masterData["yogasanaMasterData: []"]
    end
    
    subgraph ConfigureState[Configure Components State]
        userGoal["userGoal: string"]
        recommended["recommended: []"]
        selected["selected: []"]
    end
    
    subgraph PracticeState[Practice Components State]
        routine["routine: object"]
        currentIndex["currentIndex: number"]
        timeRemaining["timeRemaining: number"]
    end
    
    AppState --> ConfigureState
    AppState --> PracticeState
    
    style AppState fill:#4a90e2,color:#fff
    style ConfigureState fill:#e1f5ff
    style PracticeState fill:#fff4e1
```

## 7. Timer Logic

```mermaid
flowchart TD
    Start([Timer Starts]) --> Initialize[Set timeRemaining = duration]
    Initialize --> Interval[Start setInterval every 1s]
    
    Interval --> Decrement[timeRemaining = timeRemaining - 1]
    Decrement --> UpdateUI[Update display MM:SS]
    UpdateUI --> CheckZero{timeRemaining = 0?}
    
    CheckZero -->|No| Interval
    CheckZero -->|Yes| CallComplete[Call onComplete callback]
    CallComplete --> ClearInterval[Clear interval]
    ClearInterval --> End([Timer Complete])
    
    User[User clicks Next] -.->|Manual skip| CallComplete
    
    style Start fill:#d4edda
    style End fill:#d4edda
    style CallComplete fill:#fff3cd
```

## 8. Error Handling Flow

```mermaid
flowchart TD
    UserAction[User Action] --> Validate{Validate Input}
    
    Validate -->|Invalid| ShowError[Show error message<br/>below input]
    ShowError --> Prevent[Prevent submission]
    
    Validate -->|Valid| CallService[Call Service Function]
    CallService --> ServiceResult{Service Result}
    
    ServiceResult -->|Success| UpdateUI[Update UI<br/>Show data]
    ServiceResult -->|Error| HandleError[Show error message<br/>Log to console<br/>Provide fallback]
    
    UpdateUI --> Done([Complete])
    HandleError --> AllowRetry[Allow user to retry]
    
    style ShowError fill:#f8d7da
    style HandleError fill:#f8d7da
    style UpdateUI fill:#d4edda
    style Done fill:#d4edda
```
