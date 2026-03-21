let currentRound = 1;
const MAX_ROUNDS = 10;
let p1Score = 0;
let p2Score = 0;
let gameActive = false;
let canBuzz = false;
let buzzedPlayer = null;
let currentAnswer = null;
let gameType = window.gameType || 'math';

function startGame() {
    p1Score = 0;
    p2Score = 0;
    currentRound = 1;
    gameActive = true;
    document.getElementById('p1-score').innerText = '0';
    document.getElementById('p2-score').innerText = '0';
    if(document.getElementById('round-counter')) document.getElementById('round-counter').innerText = 'Round: 1 / 10';
    document.getElementById('pre-game-area').style.display = 'none';
    document.getElementById('game-area').style.display = 'block';
    nextProblem();
}

function nextProblem() {
    if (!gameActive) return;

    canBuzz = true;
    buzzedPlayer = null;
    document.getElementById('buzzer-area').style.display = 'flex';
    document.getElementById('options-area').style.display = 'none';
    document.getElementById('status-msg').innerText = "FIGHT FOR THE BUZZER!";
    document.getElementById('status-msg').style.color = "var(--accent)";
    document.getElementById('p1-panel').style.background = "transparent";
    document.getElementById('p2-panel').style.background = "transparent";

    const problem = generateProblem(gameType);
    document.getElementById('game-problem').innerText = problem.question;
    currentAnswer = problem.answer;
    renderOptions(problem.options);
}

function localBuzz(playerNum) {
    if (!gameActive || !canBuzz) return;

    canBuzz = false;
    buzzedPlayer = playerNum;
    
    // Fix for Memory Grid: Hide the number after buzzer is pressed
    if (gameType === 'memory') {
        document.getElementById('game-problem').innerText = "???";
    }

    document.getElementById('buzzer-area').style.display = 'none';
    document.getElementById('options-area').style.display = 'grid';
    
    const color = playerNum === 1 ? 'var(--primary-color)' : '#4a90e2';
    document.getElementById('status-msg').innerText = "PLAYER " + playerNum + " BUZZED! CHOOSE WISELY.";
    document.getElementById('status-msg').style.color = color;
    document.getElementById('p' + playerNum + '-panel').style.background = "rgba(255,255,255,0.05)";
}

function renderOptions(options) {
    const area = document.getElementById('options-area');
    area.innerHTML = '';
    options.forEach(opt => {
        const btn = document.createElement('button');
        btn.className = 'option-btn';
        btn.innerText = opt;
        btn.onclick = () => checkAnswer(opt);
        area.appendChild(btn);
    });
}

function checkAnswer(selected) {
    if (!gameActive) return;

    const isCorrect = selected == currentAnswer;
    const points = isCorrect ? 10 : -5;

    if (buzzedPlayer === 1) {
        p1Score += points;
        document.getElementById('p1-score').innerText = p1Score;
    } else {
        p2Score += points;
        document.getElementById('p2-score').innerText = p2Score;
    }

    if (isCorrect) {
        document.getElementById('status-msg').innerText = "CORRECT! +10 POINTS";
        document.getElementById('status-msg').style.color = "#00ff88";
    } else {
        document.getElementById('status-msg').innerText = "WRONG! -5 POINTS";
        document.getElementById('status-msg').style.color = "#ff3366";
    }

    if (currentRound >= MAX_ROUNDS) {
        setTimeout(endGame, 1000);
    } else {
        currentRound++;
        if(document.getElementById('round-counter')) document.getElementById('round-counter').innerText = `Round: ${currentRound} / 10`;
        setTimeout(nextProblem, 1000);
    }
}

function endGame() {
    gameActive = false;
    let winnerText = "";
    if (p1Score > p2Score) winnerText = "PLAYER 1 WINS!";
    else if (p2Score > p1Score) winnerText = "PLAYER 2 WINS!";
    else winnerText = "IT'S A TIE!";
    
    document.getElementById('game-problem').innerText = winnerText;
    document.getElementById('options-area').style.display = 'none';
    document.getElementById('status-msg').innerText = "FINAL SCORE: " + p1Score + " - " + p2Score;
    document.getElementById('status-msg').style.color = "#F5AF19";

    setTimeout(() => {
        window.location.href = '/dashboard';
    }, 5000);
}

function generateProblem(type) {
    let q, a, opts;
    const level = Math.floor((currentRound - 1) / 3) + 1;

    switch(type) {
        case 'math':
            const ops = ['+', '-', '*', '/', 'sq', 'cu'];
            const op = ops[Math.floor(Math.random() * ops.length)];
            const v1 = Math.floor(Math.random() * 20 * level) + 2;
            const v2 = Math.floor(Math.random() * 20 * level) + 2;
            
            if (op === '+') { q = `${v1} + ${v2}`; a = v1 + v2; }
            else if (op === '-') { q = `${v1} - ${v2}`; a = v1 - v2; }
            else if (op === '*') { q = `${v1 % 10 + 2} * ${v2 % 10 + 2}`; a = (v1 % 10 + 2) * (v2 % 10 + 2); }
            else if (op === '/') { a = v2 % 10 + 2; q = `${a * (v1 % 10 + 2)} / ${v1 % 10 + 2}`; }
            else if (op === 'sq') { const base = Math.floor(Math.random() * 15) + 2; q = `${base}²`; a = base * base; }
            else { const base = Math.floor(Math.random() * 10) + 2; q = `${base}³`; a = base * base * base; }
            break;
            
        case 'code':
            const code = 1000 + Math.floor(Math.random() * 9000);
            a = String(code).split('').reduce((acc, d) => acc + parseInt(d), 0);
            q = `CODE ${code}: SUM OF DIGITS?`;
            break;
            
        case 'react':
            const target = Math.floor(Math.random() * 100) + 1;
            const shift = Math.floor(Math.random() * 10) - 5;
            a = target + shift;
            q = `STIMULUS: ${target} (SHIFT=${shift}, NEXT STATE?)`;
            break;
            
        case 'memory':
            const seq = 10000 + Math.floor(Math.random() * 90000);
            const idx = Math.floor(Math.random() * 5) + 1;
            a = parseInt(String(seq)[idx - 1]);
            q = `MEMORIZE: ${seq} (DIGIT #${idx}?)`;
            break;
            
        case 'equation':
            const t = 20 + Math.floor(Math.random() * 80);
            const s = t - Math.floor(Math.random() * 20) - 5;
            a = t - s;
            q = `TARGET: ${t} (BUILD: ${s} + ?)`;
            break;
            
        case 'bluff':
            const qs = [
                {q: "Is 0.1 + 0.2 === 0.3?", a: 0}, // No (0:N, 1:Y)
                {q: "Does (x > y) imply (y < x)?", a: 1},
                {q: "Is Math.sqrt(-1) rational?", a: 0},
                {q: "Does 10 >> 1 == 5?", a: 1},
                {q: "Is null == undefined?", a: 1}
            ];
            const sel = qs[Math.floor(Math.random() * qs.length)];
            q = `${sel.q} (1:Y, 0:N)`;
            a = sel.a;
            opts = [0, 1];
            break;
            
        case 'graph':
            const ns = 5 + Math.floor(Math.random() * 5) + level;
            const gType = Math.floor(Math.random() * 3);
            if (gType === 0) {
                q = `Complete Graph K-${ns}: Total Edges?`;
                a = (ns * (ns - 1)) / 2;
            } else if (gType === 1) {
                q = `Star Graph S-${ns}: Max Degree?`;
                a = ns - 1;
            } else {
                q = `Cycle Graph C-${ns}: Total Edges?`;
                a = ns;
            }
            break;
            
        case 'ai':
            const d = 2 + Math.floor(Math.random() * 3);
            const b = 2 + Math.floor(Math.random() * 2);
            const aType = Math.floor(Math.random() * 2);
            if (aType === 0) {
                a = Math.floor((Math.pow(b, d + 1) - 1) / (b - 1));
                q = `AI Search: Tree D=${d}, B=${b}. Total Nodes?`;
            } else {
                a = Math.floor(Math.pow(b, d));
                q = `AI Search: Tree D=${d}, B=${b}. Leaf Nodes?`;
            }
            break;
            
        default:
            q = "2 + 2";
            a = 4;
    }

    if (!opts) {
        opts = [a];
        while (opts.length < 4) {
            const decoy = a + Math.floor(Math.random() * 21) - 10;
            if (!opts.includes(decoy)) opts.push(decoy);
        }
        opts = opts.sort(() => Math.random() - 0.5);
    }
    
    return { question: q, answer: a, options: opts };
}

// Global Key Listeners
document.addEventListener('keydown', (e) => {
    if (!gameActive || !canBuzz) return;
    
    if (e.key.toLowerCase() === 'q') {
        localBuzz(1);
    } else if (e.key.toLowerCase() === 'p') {
        localBuzz(2);
    }
});
