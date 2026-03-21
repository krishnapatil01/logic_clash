let stompClient = null;
let username = document.getElementById('username').innerText;
let sessionId = null;
let myScore = 0;
let opponentScore = 0;
let gameActive = false;
let canBuzz = false;

function joinQueue() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable debug for cleaner console

    stompClient.connect({}, function (frame) {
        document.getElementById('queue-status').innerText = "Status: Searching for opponent...";
        document.getElementById('join-btn').disabled = true;

        stompClient.subscribe('/topic/game/' + username, function (response) {
            const message = JSON.parse(response.body);
            handleMessage(message);
        });

        stompClient.send("/app/math/join", {}, JSON.stringify({
            sender: username,
            type: 'JOIN_QUEUE'
        }));
    });
}

function handleMessage(message) {
    const payload = message.payload;
    
    switch (message.type) {
        case 'MATCH_FOUND':
            sessionId = payload.id;
            startRound(payload);
            break;
        case 'GAME_UPDATE':
            updateGameState(payload);
            break;
        case 'BUZZED':
            handleBuzz(message.content, payload);
            break;
        case 'GAME_OVER':
            gameOver(payload);
            break;
    }
}

function startRound(session) {
    document.getElementById('pre-game-area').style.display = 'none';
    document.getElementById('game-area').style.display = 'block';
    gameActive = true;
    
    const opponent = session.player1.username === username ? session.player2.username : session.player1.username;
    document.getElementById('opponent-name').innerText = opponent;
    
    updateGameState(session);
}

function updateGameState(session) {
    // Update scores
    if (session.player1.username === username) {
        myScore = session.player1.score;
        opponentScore = session.player2.score;
    } else {
        myScore = session.player2.score;
        opponentScore = session.player1.score;
    }
    
    document.getElementById('my-score').innerText = myScore;
    document.getElementById('opponent-score').innerText = opponentScore;
    
    // Update problem
    document.getElementById('math-problem').innerText = session.currentProblem.question;
    
    // Reset Buzzer/Options UI
    document.getElementById('options-area').style.display = 'none';
    document.getElementById('buzzer-area').style.display = 'flex';
    document.getElementById('buzzer-btn').disabled = false;
    canBuzz = true;
    
    document.getElementById('status-msg').innerText = "Fight for the buzzer!";
    document.getElementById('problem-board').className = 'problem-board';
}

function buzz() {
    if (!gameActive || !canBuzz) return;
    
    stompClient.send("/app/math/buzz", {}, JSON.stringify({
        sender: username,
        type: 'BUZZED',
        payload: sessionId
    }));
}

function handleBuzz(content, session) {
    canBuzz = false;
    document.getElementById('buzzer-btn').disabled = true;
    
    const buzzedUser = session.buzzedBy;
    
    if (buzzedUser === username) {
        document.getElementById('status-msg').innerText = "YOU BUZZED! Choose the correct answer.";
        showOptions(session.currentProblem.options);
    } else {
        document.getElementById('status-msg').innerText = buzzedUser + " buzzed! Waiting for them...";
        document.getElementById('buzzer-area').style.display = 'none';
    }
}

function showOptions(options) {
    const optionsArea = document.getElementById('options-area');
    optionsArea.innerHTML = '';
    optionsArea.style.display = 'grid';
    document.getElementById('buzzer-area').style.display = 'none';

    options.forEach(opt => {
        const btn = document.createElement('button');
        btn.className = 'option-btn';
        btn.innerText = opt;
        btn.onclick = () => submitAnswer(opt);
        optionsArea.appendChild(btn);
    });
}

function submitAnswer(answer) {
    stompClient.send("/app/math/answer", {}, JSON.stringify({
        sender: username,
        type: 'SUBMIT_ANSWER',
        payload: answer
    }));
}

function gameOver(session) {
    gameActive = false;
    document.getElementById('math-problem').innerText = "GAME OVER";
    document.getElementById('buzzer-area').style.display = 'none';
    document.getElementById('options-area').style.display = 'none';
    
    const isWinner = session.winner === username;
    document.getElementById('status-msg').innerText = isWinner ? "CONGRATULATIONS! YOU WON!" : "DEFEAT! " + session.winner + " won.";
    document.getElementById('status-msg').style.color = isWinner ? "#00ff88" : "#ff3366";

    setTimeout(() => {
        window.location.href = '/dashboard';
    }, 5000);
}

// Support for keyboard buzzer (Space or Enter)
document.addEventListener('keydown', (e) => {
    if (e.code === 'Space' || e.code === 'Enter') {
        if (!document.getElementById('buzzer-btn').disabled) {
            buzz();
        }
    }
});
