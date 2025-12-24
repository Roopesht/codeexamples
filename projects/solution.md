# Solution Overview: Wellness Guide

## Folder Structure

```
wellness-guide/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Configure/
│   │   ├── Practice/
│   │   └── shared/
│   ├── services/
│   ├── data/
│   ├── App.js
│   ├── App.css
│   └── index.js
├── .env
├── .gitignore
├── package.json
└── README.md
```

## Environment Requirements

### Required Environment Variables

* **REACT_APP_GEMINI_API_KEY**: Google Gemini API key for LLM recommendations (secret: true)

### Setup Instructions

1. Create `.env` file in project root
2. Add the API key variable
3. Ensure `.env` is listed in `.gitignore`
4. Access in code via `process.env.REACT_APP_GEMINI_API_KEY`

### Notes

- The `REACT_APP_` prefix is required for Create React App to expose variables to the client
- For learning purposes, client-side API key is acceptable with free-tier API usage
- Never commit `.env` file to version control