// State Management
let state = {
    isDark: false,
    hasVoted: false,
    voter: null,
    candidates: [
        { id: 1, name: 'John Smith', party: 'Democratic Party', votes: 1234, avatar: '👨' },
        { id: 2, name: 'Sarah Johnson', party: 'Republican Party', votes: 987, avatar: '👩' },
        { id: 3, name: 'Michael Chen', party: 'Independent', votes: 756, avatar: '👨' },
        { id: 4, name: 'Emily Davis', party: 'Green Party', votes: 543, avatar: '👩' }
    ]
};

// Theme Toggle
function toggleTheme() {
    state.isDark = !state.isDark;
    document.body.classList.toggle('dark');
    document.getElementById('theme-icon').textContent = state.isDark ? '☀️' : '🌙';
    localStorage.setItem('theme', state.isDark ? 'dark' : 'light');
}

// Load theme from localStorage
function loadTheme() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        state.isDark = true;
        document.body.classList.add('dark');
        document.getElementById('theme-icon').textContent = '☀️';
    }
}

// Page Navigation
function showPage(pageName) {
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    document.getElementById(pageName).classList.add('active');
    
    // Load page-specific content
    if (pageName === 'candidates') {
        renderCandidates();
    } else if (pageName === 'results') {
        renderResults();
    }
}

// Render Candidates
function renderCandidates() {
    const container = document.getElementById('candidatesList');
    container.innerHTML = state.candidates.map(candidate => `
        <div class="candidate-card">
            <div class="candidate-avatar">${candidate.avatar}</div>
            <div class="candidate-name">${candidate.name}</div>
            <div class="candidate-party">${candidate.party}</div>
            <div class="candidate-votes">${candidate.votes} votes</div>
            <button class="btn-primary" onclick="vote(${candidate.id})" 
                ${state.hasVoted ? 'disabled' : ''}>
                ${state.hasVoted ? 'Already Voted' : 'Vote'}
            </button>
        </div>
    `).join('');
}

// Vote Function
function vote(candidateId) {
    if (!state.voter) {
        showToast('Please login first!');
        return;
    }
    
    if (state.hasVoted) {
        showToast('You have already voted!');
        return;
    }
    
    fetch('http://localhost:5000/api/vote', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ candidateId, voterId: state.voter.voterId })
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            const candidate = state.candidates.find(c => c.id === candidateId);
            if (candidate) {
                candidate.votes++;
                state.hasVoted = true;
                state.voter.hasVoted = true;
                localStorage.setItem('voter', JSON.stringify(state.voter));
                showToast(`Vote cast for ${candidate.name}!`);
                document.getElementById('votingStatus').textContent = `Voted for ${candidate.name}`;
                renderCandidates();
            }
        } else {
            showToast(data.error || 'Vote failed');
        }
    })
    .catch(() => showToast('Connection error'));
}

// Render Results
function renderResults() {
    const totalVotes = state.candidates.reduce((sum, c) => sum + c.votes, 0);
    const winner = state.candidates.reduce((max, c) => c.votes > max.votes ? c : max);
    
    // Winner Card
    document.getElementById('winnerCard').innerHTML = `
        <div style="font-size: 64px; margin-bottom: 16px;">${winner.avatar}</div>
        <h2>🏆 Winner: ${winner.name}</h2>
        <p style="font-size: 24px; margin-top: 8px;">${winner.party}</p>
        <p style="font-size: 32px; font-weight: bold; margin-top: 16px;">${winner.votes} votes</p>
    `;
    
    // Results List
    const sortedCandidates = [...state.candidates].sort((a, b) => b.votes - a.votes);
    document.getElementById('resultsList').innerHTML = sortedCandidates.map(candidate => {
        const percentage = ((candidate.votes / totalVotes) * 100).toFixed(1);
        return `
            <div class="result-bar">
                <div style="font-size: 48px;">${candidate.avatar}</div>
                <div class="result-info">
                    <div class="result-name">${candidate.name}</div>
                    <div class="result-progress">
                        <div class="result-progress-bar" style="width: ${percentage}%"></div>
                    </div>
                    <div style="margin-top: 8px; color: #6b7280;">${percentage}%</div>
                </div>
                <div class="result-votes">${candidate.votes}</div>
            </div>
        `;
    }).join('');
}

// Admin Functions
function addCandidate() {
    const name = document.getElementById('candidateName').value;
    const party = document.getElementById('candidateParty').value;
    
    if (!name || !party) {
        showToast('Please fill all fields!');
        return;
    }
    
    const avatars = ['👨', '👩', '🧑', '👴', '👵'];
    const newCandidate = {
        id: state.candidates.length + 1,
        name,
        party,
        votes: 0,
        avatar: avatars[Math.floor(Math.random() * avatars.length)]
    };
    
    state.candidates.push(newCandidate);
    
    document.getElementById('candidateName').value = '';
    document.getElementById('candidateParty').value = '';
    
    showToast(`Candidate ${name} added successfully!`);
}

function startElection() {
    showToast('Election started!');
}

function endElection() {
    showToast('Election ended!');
}

// Toast Notification
function showToast(message) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Countdown Timer
function updateCountdown() {
    const endDate = new Date();
    endDate.setDate(endDate.getDate() + 2);
    endDate.setHours(endDate.getHours() + 14);
    
    setInterval(() => {
        const now = new Date();
        const diff = endDate - now;
        
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        
        const countdownEl = document.getElementById('countdown');
        if (countdownEl) {
            countdownEl.textContent = `${days}d ${hours}h ${minutes}m`;
        }
    }, 1000);
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadTheme();
    updateCountdown();
    renderCandidates();
});

// Auth Functions
function checkAuth() {
    const voter = localStorage.getItem('voter');
    if (!voter) {
        window.location.href = 'login.html';
        return;
    }
    state.voter = JSON.parse(voter);
    state.hasVoted = state.voter.hasVoted;
    document.getElementById('logoutBtn').style.display = 'block';
}

function logout() {
    localStorage.removeItem('voter');
    window.location.href = 'login.html';
}
