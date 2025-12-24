# Architecture: Wellness Guide

## 1. Application Flow

```mermaid
graph TD
    Start["User Opens App"] --> ModeSelect{"Select Mode"}
    ModeSelect -->|Configure| ConfigMode["Configure Mode"]
    ModeSelect -->|Practice| PracticeMode["Practice Mode"]
    
    ConfigMode --> EnterGoal["Enter Wellness Goal"]
    EnterGoal --> GetRecs["Get LLM Recommendations"]
    GetRecs --> SelectYoga["Select Yogasanas"]
    SelectYoga --> SetDuration["Set Durations"]
    SetDuration --> SaveRoutine["Save to localStorage"]
    SaveRoutine --> Done1["Routine Saved"]
    
    PracticeMode --> LoadRoutine["Load Saved Routine"]
    LoadRoutine --> DisplayYoga["Display Yogasana"]
    DisplayYoga --> StartTimer["Start Timer"]
    StartTimer --> TimerDone{"Timer = 0?"}
    TimerDone -->|No| StartTimer
    TimerDone -->|Yes| MoreYoga{"More Yogasanas?"}
    MoreYoga -->|Yes| DisplayYoga
    MoreYoga -->|No| Complete["Routine Complete"]
    
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
graph TD
    UI["Presentation Layer<br/>React Components"]
    
    UI --> GoalInputComp["GoalInput"]
    UI --> RecListComp["RecommendationList"]
    UI --> RoutineFormComp["RoutineForm"]
    UI --> PracticeComp["PracticeSession"]
    UI --> TimerComp["Timer"]
    
    GoalInputComp --> LLMService["llmService.js"]
    RecListComp --> YogaService["yogasanaService.js"]
    RoutineFormComp --> StorageService["storageService.js"]
    PracticeComp --> YogaService
    PracticeComp --> StorageService
    
    LLMService --> GeminiAPI["Google Gemini API"]
    StorageService --> LocalStorage["localStorage"]
    YogaService --> JSONData["yogasanas.json"]
    
    classDef uiStyle fill:#61DAFB,stroke:#333,stroke-width:2px
    classDef serviceStyle fill:#98FB98,stroke:#333,stroke-width:2px
    classDef dataStyle fill:#FFB6C1,stroke:#333,stroke-width:2px
    
    class UI,GoalInputComp,RecListComp,RoutineFormComp,PracticeComp,TimerComp uiStyle
    class LLMService,StorageService,YogaService serviceStyle
    class GeminiAPI,LocalStorage,JSONData dataStyle
```

## 6. State Management

```mermaid
graph TD
    AppState["App Component<br/>mode, yogasanas"]
    
    AppState --> ConfigState["Configure Components<br/>recommendations, selectedYogasanas"]
    AppState --> PracticeState["Practice Components<br/>routine, currentIndex"]
    
    ConfigState --> GoalState["GoalInput<br/>goalText, isLoading, error"]
    ConfigState --> RecState["RecommendationList<br/>selectedIds"]
    ConfigState --> FormState["RoutineForm<br/>durations, error, successMessage"]
    
    PracticeState --> SessionState["PracticeSession<br/>routine, currentIndex, isComplete"]
    PracticeState --> TimerState["Timer<br/>timeRemaining"]
    
    FormState --> LS["localStorage<br/>wellness_routine"]
    SessionState --> LS
    
    classDef appStyle fill:#4a90e2,color:#fff,stroke:#333,stroke-width:3px
    classDef flowStyle fill:#e1f5ff,stroke:#333,stroke-width:2px
    classDef compStyle fill:#fff4e1,stroke:#333,stroke-width:2px
    classDef storageStyle fill:#d4edda,stroke:#333,stroke-width:2px
    
    class AppState appStyle
    class ConfigState,GoalState,RecState,FormState flowStyle
    class PracticeState,SessionState,TimerState compStyle
    class LS storageStyle
```

## 7. Timer Logic

```mermaid
graph TD
    Start["Timer Component Mounts"] --> InitTime["Initialize timeRemaining<br/>from durationSeconds prop"]
    InitTime --> CreateInterval["Create setInterval<br/>1000ms interval"]
    CreateInterval --> Decrement["Decrement timeRemaining<br/>by 1 second"]
    Decrement --> CheckZero{"timeRemaining<br/>== 0?"}
    CheckZero -->|No| FormatTime["Format as MM:SS<br/>Display to user"]
    FormatTime --> Decrement
    CheckZero -->|Yes| CallComplete["Call onComplete<br/>callback"]
    CallComplete --> ClearInterval["Clear interval"]
    ClearInterval --> End["Timer Ends"]
    
    ManualSkip["User Clicks Next"] --> CallComplete
    PropChange["durationSeconds<br/>prop changes"] --> InitTime
    
    classDef startStyle fill:#98FB98,stroke:#333,stroke-width:2px
    classDef processStyle fill:#87CEEB,stroke:#333,stroke-width:2px
    classDef endStyle fill:#FFB6C1,stroke:#333,stroke-width:2px
    classDef userStyle fill:#FFD700,stroke:#333,stroke-width:2px
    
    class Start,InitTime,PropChange startStyle
    class CreateInterval,Decrement,FormatTime,CheckZero processStyle
    class CallComplete,ClearInterval,End endStyle
    class ManualSkip userStyle
```

## 8. Error Handling Flow

```mermaid
graph TD
    UserAction["User Action"] --> ValidateInput["Validate Input"]
    ValidateInput --> InputValid{"Input Valid?"}
    
    InputValid -->|No| ShowError["Display Error Message<br/>Below Input"]
    ShowError --> WaitFix["Wait for User<br/>to Fix Input"]
    WaitFix --> UserAction
    
    InputValid -->|Yes| APICall["Make API Call<br/>or Service Call"]
    APICall --> TryCatch["Try-Catch Block"]
    
    TryCatch --> Success{"Success?"}
    Success -->|Yes| ProcessData["Process Response"]
    ProcessData --> UpdateUI["Update UI State"]
    UpdateUI --> ShowSuccess["Show Success Message"]
    
    Success -->|No| CatchError["Catch Error"]
    CatchError --> LogError["Console.log Error"]
    LogError --> ShowAPIError["Display User-Friendly<br/>Error Message"]
    ShowAPIError --> AllowRetry["Allow User to Retry"]
    AllowRetry --> UserAction
    
    classDef userStyle fill:#FFD700,stroke:#333,stroke-width:2px
    classDef validateStyle fill:#87CEEB,stroke:#333,stroke-width:2px
    classDef successStyle fill:#98FB98,stroke:#333,stroke-width:2px
    classDef errorStyle fill:#FFB6C1,stroke:#333,stroke-width:2px
    
    class UserAction,WaitFix,AllowRetry userStyle
    class ValidateInput,InputValid,APICall,TryCatch,Success validateStyle
    class ProcessData,UpdateUI,ShowSuccess successStyle
    class ShowError,CatchError,LogError,ShowAPIError errorStyle
```
