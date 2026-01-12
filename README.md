# 🗳️ Blockchain-Based Voting System

A secure, transparent, and decentralized voting system built with vanilla JavaScript, Java backend, MongoDB database, and blockchain technology.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green.svg)

## ✨ Features

- 🔐 **Secure Authentication** - User registration and login with password hashing
- ⛓️ **Blockchain Integration** - Every vote recorded on immutable blockchain
- 💾 **MongoDB Database** - Persistent data storage
- 🎨 **Modern UI** - Glassmorphism design with dark/light mode
- 📊 **Live Results** - Real-time vote counting and visualization
- 👨‍💼 **Admin Panel** - Manage candidates and elections
- 📱 **Responsive Design** - Works on all devices
- 🔍 **Blockchain Explorer** - View and verify blockchain data

## 🛠️ Tech Stack

### Frontend
- HTML5, CSS3, Vanilla JavaScript
- No frameworks or build tools required

### Backend
- Java 17
- HTTP Server (com.sun.net.httpserver)
- MongoDB Java Driver

### Database
- MongoDB (Local)

### Blockchain
- Custom implementation with SHA-256 hashing
- Proof of Work mining

## 📁 Project Structure

```
blockchain-voting-system/
├── backend/
│   ├── src/main/java/
│   │   ├── VotingServer.java      # Main server
│   │   ├── Blockchain.java        # Blockchain implementation
│   │   ├── MongoDB.java           # Database operations
│   │   └── UserAuth.java          # Authentication
│   ├── run.bat                    # Run script
│   └── download-mongo.bat         # Download MongoDB driver
├── index.html                     # Main voting app
├── login.html                     # Login page
├── register.html                  # Registration page
├── blockchain.html                # Blockchain explorer
├── styles.css                     # All styling
└── script.js                      # Frontend logic
```

## 🚀 Getting Started

### Prerequisites

1. **Java JDK 17+**
   - Download: https://adoptium.net/

2. **MongoDB**
   - Download: https://www.mongodb.com/try/download/community
   - Install and start MongoDB service

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Anish4304/blockchain-voting-system.git
cd blockchain-voting-system
```

2. **Download MongoDB Java Driver**
```bash
cd backend
download-mongo.bat
```

3. **Start MongoDB** (if not running)
```bash
net start MongoDB
```

4. **Run Backend**
```bash
cd backend
run.bat
```

5. **Open Frontend**
- Open `register.html` in your browser
- Register a new account
- Login and start voting!

## 📖 Usage

### For Voters

1. **Register** - Create account with full name, email, voter ID, and password
2. **Login** - Authenticate with email/voter ID and password
3. **Vote** - Browse candidates and cast your vote
4. **View Results** - See live election results with progress bars

### For Admins

1. Navigate to Admin Panel
2. Add new candidates with name and party
3. Start/End elections
4. Monitor voting activity

### Blockchain Explorer

- Open `blockchain.html` to view the blockchain
- See all blocks with hashes, timestamps, and data
- Verify chain integrity

## 🔒 Security Features

- **Password Hashing** - SHA-256 encryption
- **One Vote Per User** - Enforced at database level
- **Blockchain Verification** - Tamper-proof vote records
- **Session Management** - Secure localStorage tokens

## 📊 Database Schema

### Collections

**users**
```javascript
{
  fullName: String,
  email: String,
  voterId: String,
  passwordHash: String,
  hasVoted: Boolean
}
```

**candidates**
```javascript
{
  id: Number,
  name: String,
  party: String,
  avatar: String,
  votes: Number
}
```

**votes**
```javascript
{
  voterId: String,
  candidateId: Number,
  candidateName: String,
  timestamp: Number
}
```

## 🌐 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/register` | Register new voter |
| POST | `/api/login` | Authenticate voter |
| GET | `/api/candidates` | Get all candidates |
| POST | `/api/candidates` | Add new candidate |
| POST | `/api/vote` | Cast a vote |
| GET | `/api/results` | Get election results |
| GET | `/api/blockchain` | Get blockchain data |

## 🎨 Screenshots

### Landing Page
Modern hero section with statistics

### Voting Interface
Clean candidate cards with vote buttons

### Results Dashboard
Live results with progress bars

### Blockchain Explorer
View all blocks and verify integrity

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- MongoDB for database
- Java HttpServer for backend
- Blockchain technology for transparency

## 📧 Contact

Anish4304 - GitHub: https://github.com/Anish4304

Project Link: https://github.com/Anish4304/blockchain-voting-system

---

**Built with ❤️ for transparent and secure elections**
