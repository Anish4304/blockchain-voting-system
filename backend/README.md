# Java Backend

Simple Java HTTP server for blockchain voting system.

## Requirements

- Java JDK 8 or higher

## Run

```bash
cd backend
run.bat
```

Server starts on http://localhost:5000

## API Endpoints

- GET `/api/candidates` - Get all candidates
- POST `/api/vote` - Cast a vote
- POST `/api/candidates` - Add new candidate
- GET `/api/results` - Get election results
