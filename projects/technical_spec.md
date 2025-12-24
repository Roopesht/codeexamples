# Technical Specification: Wellness Guide

## 1. System Architecture

### 1.1 Application Type
- Single-page application (SPA) built with React
- Client-side only, no server infrastructure
- Runs in web browser environment

### 1.2 Architectural Layers

**Presentation Layer**
- React components for UI rendering
- Manages user interactions and visual state
- Delegates data operations to service layer

**Service Layer**
- API communication logic (LLM calls)
- Data transformation and validation
- Storage operations (read/write to localStorage)

**Data Layer**
- localStorage for routine persistence
- Static JSON file for yogasana master data
- No external database

## 2. Technology Stack

### 2.1 Core Framework
- **React**: Component-based UI library
- **JavaScript (ES6+)**: Application logic
- **CSS**: Styling (no framework constraint)

### 2.2 External Services
- **Google Gemini API**: LLM for safety screening and recommendations
- **Environment Variables**: API key management (`.env` file)

### 2.3 Browser APIs
- **localStorage**: Routine persistence
- **fetch API**: HTTP requests to LLM service

## 3. Data Design

### 3.1 Yogasana Master Data Structure
```json
{
  "yogasanas": [
    {
      "id": "string (e.g., 'warrior-pose')",
      "name": "string (English name)",
      "benefit": "string (one sentence)",
      "steps": ["string array, 3-5 steps"],
      "imageUrl": "string (URL to image)",
      "suggestedDuration": "number (seconds)"
    }
  ]
}
```

**Storage Location**: Static JSON file in `/src/data/` folder
**Size**: 15-20 yogasanas maximum

### 3.2 User Routine Structure
```json
{
  "selectedYogasanas": [
    {
      "yogasana": "string (references master data)",
      "durationSeconds": "number (user-assigned seconds)"
    }
  ],
  "createdAt": "ISO 8601 timestamp"
}
```

**Storage Location**: localStorage key `wellness_routine` (singular)
**Note**: Only ONE routine stored at a time

## 4. Interface Contracts

### 4.1 LLM API Request Format

**Single Recommendation Request**
```
Endpoint: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
Method: POST
Headers: 
  - Content-Type: application/json

Query Parameter:
  - key=[API_KEY from environment]

Body:
{
  "contents": [
    {
      "parts": [
        {
          "text": "Recommend 5-6 yogasanas for: [user goal]. Choose only from: Mountain Pose, Warrior Pose, Child Pose, Tree Pose, Cat Pose, Cow Pose, Downward Dog, Cobra Pose, Bridge Pose, Corpse Pose. Return only the names separated by commas."
        }
      ]
    }
  ]
}
```

### 4.2 LLM API Response Format

**Expected Response Structure**
```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "Mountain Pose, Warrior Pose, Child Pose, Tree Pose, Downward Dog"
          }
        ]
      }
    }
  ]
}
```

**Error Response**
```json
{
  "error": {
    "code": 400,
    "message": "API key not valid",
    "status": "INVALID_ARGUMENT"
  }
}
```

### 4.3 localStorage Interface

**Save Routine**
```javascript
const routine = {
  selectedYogasanas: [...],
  createdAt: new Date().toISOString()
};
localStorage.setItem("wellness_routine", JSON.stringify(routine));
```

**Load Routine**
```javascript
const routine = JSON.parse(localStorage.getItem("wellness_routine"));
// Returns null if no routine saved
```

## 5. Component Responsibilities

### 5.1 Component Hierarchy (Simplified for Beginners)

**App Component**
- Manages current mode state (Configure or Practice)
- Renders ModeSelector
- Conditionally renders Configure or Practice components

**Configure Components**
- **GoalInput**: Text input and submit button for wellness goal
- **RecommendationList**: Displays LLM-suggested yogasanas with checkboxes
- **RoutineForm**: Shows selected yogasanas with duration inputs and Save button

**Practice Components**
- **PracticeSession**: Displays one yogasana at a time with timer
- **Timer**: Countdown display with auto-advance logic

**Shared Components**
- **ModeSelector**: Two buttons to switch modes
- **YogasanaCard**: Displays yogasana image, name, benefit

### 5.2 Service Modules

**llmService.js**
- `getRecommendations(goal, yogasanaNames)`: Makes single API call, returns array of yogasana names

**storageService.js**
- `saveRoutine(routine)`: Saves to localStorage
- `loadRoutine()`: Retrieves from localStorage, returns null if none exists

**yogasanaService.js**
- `loadMasterData()`: Imports yogasana JSON file
- `getYogasanaById(id)`: Returns single yogasana object

## 6. State Management Strategy

### 6.1 What State to Track
- **Current Mode**: "configure" or "practice" (stored in App component)
- **User's Goal**: Text input from user (in GoalInput component)
- **Recommended Yogasanas**: Array of yogasana IDs from LLM (in Configure parent)
- **Selected Yogasanas**: Array of selected yogasanas with durations (in Configure parent)
- **Current Yogasana Index**: Which yogasana is showing in practice (in PracticeSession)
- **Timer Remaining**: Seconds left on current yogasana (in Timer component)

### 6.2 State Storage Rules
- **UI state** (loading, errors, current screen): Keep in component state using useState
- **Saved routine data**: Always read from/write to localStorage, never keep in state
- **Master yogasana data**: Load once when app starts, keep in state

### 6.3 State Flow Example
1. User selects yogasanas and clicks Save
2. Component calls `storageService.saveRoutine()`
3. Service writes to localStorage
4. Component shows success message
5. When user switches to Practice mode:
   - PracticeSession calls `storageService.loadRoutine()`
   - Displays first yogasana from loaded routine

## 7. Runtime Constraints

### 7.1 Performance Expectations
- LLM API response: 5-10 seconds (show "Loading..." message)
- Timer accuracy: Updates every second
- Page should not freeze during API call

### 7.2 Data Limits
- localStorage: Can store 100+ routines easily (5MB limit)
- Yogasana master file: 15-20 yogasanas
- Image files: Hosted externally (not in project)

### 7.3 Browser Support
- Modern browsers (Chrome, Firefox, Safari, Edge)
- Must support: React, localStorage, fetch API

### 7.4 Network Requirements
- Internet needed only when getting recommendations
- Practice mode works offline if routine already saved

## 8. Security Considerations

### 8.1 API Key Protection
- Store API key in `.env` file
- Add `.env` to `.gitignore` so it's never committed
- Access key using `process.env.REACT_APP_GEMINI_API_KEY`
- Note: For learning purposes, client-side API key is acceptable with free-tier API

### 8.2 Data Privacy
- No user data sent to servers except the LLM API call (just the goal text)
- All routines stay in user's browser localStorage
- No tracking or analytics

### 8.3 Input Validation
- Check that goal is not empty before API call
- Check that duration is a positive number before saving

## 9. Error Handling Strategy

### 9.1 Error Types and Responses

**LLM API Errors**
- Show message: "Could not get recommendations. Please try again."
- Let user still see all yogasanas and select manually

**Empty Input Errors**
- Show message: "Please enter a wellness goal"
- Disable submit button until input is filled

**No Saved Routine**
- Show message in Practice mode: "No routine found. Create one in Configure mode first."

### 9.2 Error Display
- Show errors in red text below the relevant input
- Don't crash the app - just show a message
- Log errors to console for debugging

## 10. Development Guidelines

### 10.1 What NOT to Include
- No user login or authentication
- No backend server or database
- No CSS frameworks (write simple CSS yourself)
- No complex routing (just mode switching)

### 10.2 React Patterns to Use
- Functional components with hooks (useState, useEffect)
- Props to pass data from parent to child
- Separate components for different parts of the UI
- Separate service files for API and storage logic

### 10.3 Suggested File Structure
```
src/
  components/
    App.js
    ModeSelector.js
    Configure/
      GoalInput.js
      RecommendationList.js
      RoutineForm.js
    Practice/
      PracticeSession.js
      Timer.js
    shared/
      YogasanaCard.js
  services/
    llmService.js
    storageService.js
    yogasanaService.js
  data/
    yogasanas.json
  App.css
  index.js
```

## 11. Environment Setup

### 11.1 Required Tools
- Node.js (for Create React App)
- Text editor (VS Code recommended)
- Modern web browser

### 11.2 Environment Variables
Create `.env` file in project root:
```
REACT_APP_GEMINI_API_KEY=your_api_key_here
```

### 11.3 Getting Started
```bash
npx create-react-app wellness-guide
cd wellness-guide
# Add .env file with API key
npm start
```