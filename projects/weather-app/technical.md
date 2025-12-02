# Weather API - Technical Document

## 1. Technology Stack
- **Runtime**: Node.js (v14+)
- **Framework**: Express.js
- **HTTP Client**: Axios
- **Middleware**: CORS, dotenv
- **External API**: WeatherAPI.com or OpenWeatherMap

## 2. Project Structure
```
svc/
├── app.js          # Express app setup
├── server.js       # Server startup
├── controllers/
│   └── weatherController.js
├── routes/
│   └── weatherRoutes.js
├── .env            # Environment variables
└── package.json
```

## 3. API Endpoints

### GET /api/health
**Response**: Service status check
```json
{"status":"healthy","timestamp":"ISO_DATE"}
```

### GET /api/weather/current?city={city}
**Parameters**: `city` (required)
**Response**: Current weather data
```json
{
  "location": "City, Country",
  "temperature": "22°C",
  "conditions": "Sunny",
  "humidity": 65
}
```

## 4. Core Implementation

### 4.1 app.js - Express Configuration
```javascript
const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());
// Routes will be added here

module.exports = app;
```

### 4.2 server.js - Server Startup
```javascript
require('dotenv').config();
const app = require('./app');

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
```

### 4.3 Weather Controller
```javascript
const axios = require('axios');

async function getCurrentWeather(city) {
  const apiKey = process.env.WEATHER_API_KEY;
  const url = `https://api.weatherapi.com/v1/current.json?key=${apiKey}&q=${city}`;
  
  const response = await axios.get(url);
  return {
    location: `${response.data.location.name}, ${response.data.location.country}`,
    temperature: `${response.data.current.temp_c}°C`,
    conditions: response.data.current.condition.text,
    humidity: response.data.current.humidity
  };
}
```

## 5. Environment Variables
```bash
PORT=3000
WEATHER_API_KEY=your_api_key_here
```

## 6. Setup Commands
```bash
# Initialize project
npm init -y
npm install express cors dotenv axios

# Create files
mkdir -p svc/controllers svc/routes
touch svc/app.js svc/server.js .env

# Run server
node svc/server.js
```

## 7. Error Handling
- **400**: Invalid/missing city parameter
- **404**: City not found
- **500**: External API failure
- **200**: Success with weather data

## 8. Testing
```bash
# Test health endpoint
curl http://localhost:3000/api/health

# Test weather endpoint
curl "http://localhost:3000/api/weather/current?city=London"
```

## 9. Security Notes
- Store API keys in .env (add .env to .gitignore)
- Enable CORS for web client access
- Validate all user inputs
- Use HTTPS in production
