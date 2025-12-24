# Functional Specification: Wellness Guide

## 1. System Purpose

Wellness Guide helps users create a simple yoga routine and practice it with a guided timer. The app demonstrates basic React concepts through a focused wellness application.

## 2. Core Capabilities

### 2.1 Mode Selection
- Users switch between Configure mode (plan routine) and Practice mode (execute routine)
- Simple button or tab to change modes

### 2.2 Wellness Goal Input (Configure Mode)
- Users enter their wellness goal in a text area (e.g., "I am 30 years old with 5'7" and 75 kg, I want to improve flexibility and my eyesight")
- One-time input with a submit button
- Medical disclaimer displayed: "This app provides wellness guidance, not medical advice. Consult a healthcare professional for medical concerns."

### 2.3 Yogasana Recommendations (Configure Mode)
- LLM suggests 5-6 yogasanas based on the user's goal
- Recommendations show name, brief benefit, and small image
- System matches LLM suggestions to pre-loaded yogasana data

### 2.4 Routine Builder (Configure Mode)
- Users click checkboxes to select 3-5 yogasanas from recommendations
- Users enter duration (in seconds) for each selected yogasana
- One "Save Routine" button to store the routine
- Routine saved to localStorage (replaces any previous routine)

### 2.5 Practice Session (Practice Mode)
- Displays one yogasana at a time with large image and instructions
- Shows countdown timer with the assigned duration
- Timer automatically moves to next yogasana when it reaches zero
- "Next" button to manually skip ahead
- Shows "Routine Complete!" message at the end

### 2.6 Data Persistence
- One routine saved in localStorage
- If user creates a new routine, it overwrites the previous one
- Routine automatically loads when Practice mode is opened

## 3. User Journeys

### Journey A: Creating a Routine
1. User opens app (starts in Configure mode)
2. User enters wellness goal (e.g., "stress relief")
3. User clicks "Get Recommendations" button
4. System displays 5-6 yogasanas with images and benefits
5. User selects 3-5 yogasanas using checkboxes
6. User enters duration in seconds for each selected yogasana
7. User clicks "Save Routine" button
8. System shows success message

### Journey B: Practicing the Routine
1. User clicks Practice mode button
2. System automatically loads the saved routine
3. System displays first yogasana with large image and instructions
4. Countdown timer starts automatically
5. When timer reaches zero, system shows next yogasana
6. User can click "Next" button to skip ahead
7. After last yogasana, system shows "Routine Complete!" message

## 4. External Dependencies

### 4.1 LLM API Integration
- **Purpose**: Generate personalized yogasana recommendations only
- **Provider**: Google Gemini (or compatible LLM API)
- **Authentication**: API key via environment variable
- **Usage**: Single API call when user submits their wellness goal
- **Response**: Simple text listing 5-6 yogasana names from the library

### 4.2 Yogasana Master Data
- Pre-generated JSON file (15-20 yogasanas maximum)
- Structure per yogasana:
  - Unique ID
  - Name (English)
  - Brief benefit description (1 sentence)
  - Step-by-step instructions (3-5 steps)
  - Image URL
  - Default suggested duration

## 5. Error Handling

### 5.1 LLM API Failures
- **Scenario**: Network error or invalid API key
- **Response**: Show simple error message: "Could not get recommendations. Please check your connection and try again."
- **Fallback**: User can still manually select from all yogasanas in the library

### 5.2 Invalid User Input
- **Scenario**: Empty goal or non-numeric duration
- **Response**: Show validation message below the field, prevent form submission

### 5.3 No Saved Routine
- **Scenario**: User opens Practice mode without saving a routine first
- **Response**: Display message: "No routine found. Please create a routine in Configure mode first."

## 6. UI Interaction Expectations

### 6.1 Mode Selector
- Two buttons: "Configure" and "Practice"
- Clicking switches the view immediately

### 6.2 Goal Input Screen (Configure Mode)
- Text input box with placeholder text
- "Get Recommendations" button
- Disclaimer text displayed prominently

### 6.3 Recommendation Display (Configure Mode)
- List of 5-6 yogasanas
- Each shows: small image, name, brief benefit
- Checkbox next to each yogasana
- Duration input field appears when checkbox is selected

### 6.4 Routine Builder (Configure Mode)
- Shows currently selected yogasanas with duration inputs
- Single "Save Routine" button at bottom
- Success message appears after saving

### 6.5 Practice Display (Practice Mode)
- Large yogasana image at top
- Yogasana name as heading
- Step-by-step instructions listed below
- Large countdown timer (e.g., "0:45" remaining)
- "Next" button to skip
- Progress indicator (e.g., "Yogasana 2 of 4")

## 7. Behavioral Rules

### 7.1 Responsible AI Usage
- Display disclaimer before recommendations: "This app provides wellness guidance, not medical advice"
- LLM only recommends yogasanas that exist in the master data file

### 7.2 Data Handling
- Only one routine stored at a time
- Creating new routine overwrites the previous one
- All data stays on user's device (localStorage)

## 8. System Constraints

- No user authentication or login required
- No database or backend server
- No network connectivity required except for LLM API calls
- Runs entirely in browser environment
- Compatible with modern browsers (Chrome, Firefox, Safari, Edge)