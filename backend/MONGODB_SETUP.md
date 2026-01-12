# MongoDB Setup

## Install MongoDB

1. Download MongoDB Community Server: https://www.mongodb.com/try/download/community
2. Install with default settings
3. MongoDB will run as a Windows service on port 27017

## Verify Installation

```bash
mongod --version
```

## Start MongoDB (if not running)

```bash
net start MongoDB
```

## Download MongoDB Java Driver

Run in backend folder:
```bash
download-mongo.bat
```

## Compile Backend

```bash
cd backend
javac -cp ".;mongodb-driver-sync-4.11.1.jar;mongodb-driver-core-4.11.1.jar;bson-4.11.1.jar" src\main\java\*.java -d .
```

## Run Backend

```bash
java -cp ".;mongodb-driver-sync-4.11.1.jar;mongodb-driver-core-4.11.1.jar;bson-4.11.1.jar" VotingServer
```

## Database Structure

- **Database:** voting_system
- **Collections:**
  - users (fullName, email, voterId, passwordHash, hasVoted)
  - candidates (id, name, party, avatar, votes)
  - votes (voterId, candidateId, candidateName, timestamp)
