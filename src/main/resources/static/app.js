/**
 * IndiKore Suite - Frontend Controller
 * Connects the modern dashboard to the embedded Java HttpServer REST APIs.
 */

document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    initCalculator();
    initTextStudio();
    initTelemetry();
});

/* ==========================================================================
   1. Tab Navigation
   ========================================================================== */
function initTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanels = document.querySelectorAll('.tab-panel');

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetPanelId = btn.getAttribute('aria-controls');

            tabButtons.forEach(b => {
                b.classList.remove('active');
                b.setAttribute('aria-selected', 'false');
            });
            tabPanels.forEach(p => {
                p.classList.remove('active');
                p.hidden = true;
            });

            btn.classList.add('active');
            btn.setAttribute('aria-selected', 'true');

            const targetPanel = document.getElementById(targetPanelId);
            if (targetPanel) {
                targetPanel.classList.add('active');
                targetPanel.hidden = false;
            }
        });
    });
}

/* ==========================================================================
   2. Mathematical Engine (Calculator)
   ========================================================================== */
function initCalculator() {
    const inputA = document.getElementById('calc-input-a');
    const inputB = document.getElementById('calc-input-b');
    const groupValB = document.getElementById('group-val-b');
    const opChips = document.querySelectorAll('.op-chip');
    const submitBtn = document.getElementById('calc-submit-btn');
    const clearBtn = document.getElementById('calc-clear-btn');
    const equationDisplay = document.getElementById('calc-equation');
    const resultDisplay = document.getElementById('calc-result-display');
    const historyList = document.getElementById('calc-history-list');
    const clearHistoryBtn = document.getElementById('clear-history-btn');
    const presetChips = document.querySelectorAll('.preset-chips .chip');

    let currentOp = 'add';
    const history = [];

    // Switch active operation chip
    opChips.forEach(chip => {
        chip.addEventListener('click', () => {
            opChips.forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            currentOp = chip.dataset.op;

            // Factorial & Prime check only need 1 input
            if (currentOp === 'factorial' || currentOp === 'isPrime') {
                groupValB.style.opacity = '0.35';
                inputB.disabled = true;
                inputB.placeholder = 'N/A';
            } else {
                groupValB.style.opacity = '1';
                inputB.disabled = false;
                inputB.placeholder = 'e.g. 5';
            }
        });
    });

    // Compute via Java API
    async function performCalculation() {
        const valA = parseFloat(inputA.value);
        const valB = parseFloat(inputB.value);

        if (isNaN(valA)) {
            showToast('Please enter a valid numeric value for A', 'error');
            inputA.focus();
            return;
        }

        if ((currentOp !== 'factorial' && currentOp !== 'isPrime') && isNaN(valB)) {
            showToast('Please enter a valid numeric value for B', 'error');
            inputB.focus();
            return;
        }

        submitBtn.disabled = true;
        submitBtn.style.opacity = '0.7';

        try {
            const response = await fetch('/api/calculate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    operation: currentOp,
                    a: valA,
                    b: isNaN(valB) ? 0 : valB
                })
            });

            const data = await response.json();

            if (!response.ok || !data.success) {
                showToast(data.error || 'Computation error', 'error');
                resultDisplay.textContent = 'Error';
                equationDisplay.textContent = data.error || 'Computation Failed';
                return;
            }

            // Build display equation
            let equationText = '';
            switch (currentOp) {
                case 'add': equationText = `${valA} + ${valB} =`; break;
                case 'subtract': equationText = `${valA} - ${valB} =`; break;
                case 'multiply': equationText = `${valA} * ${valB} =`; break;
                case 'divide': equationText = `${valA} / ${valB} =`; break;
                case 'power': equationText = `${valA}^${valB} =`; break;
                case 'factorial': equationText = `${valA}! =`; break;
                case 'isPrime': equationText = `isPrime(${valA}) =`; break;
            }

            equationDisplay.textContent = equationText;
            resultDisplay.textContent = data.result;

            // Push to computation history
            addToHistory(equationText, data.result);
        } catch (err) {
            showToast('Unable to connect to Java backend API', 'error');
            console.error('API Error:', err);
        } finally {
            submitBtn.disabled = false;
            submitBtn.style.opacity = '1';
        }
    }

    submitBtn.addEventListener('click', performCalculation);

    // Keyboard support: Enter key in input triggers compute
    [inputA, inputB].forEach(input => {
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                performCalculation();
            }
        });
    });

    // Clear inputs
    clearBtn.addEventListener('click', () => {
        inputA.value = '';
        inputB.value = '';
        inputA.focus();
    });

    // Quick presets
    presetChips.forEach(chip => {
        chip.addEventListener('click', () => {
            const op = chip.dataset.op;
            const a = chip.dataset.a;
            const b = chip.dataset.b;

            const targetOpChip = document.querySelector(`.op-chip[data-op="${op}"]`);
            if (targetOpChip) targetOpChip.click();

            inputA.value = a;
            inputB.value = b;
            performCalculation();
        });
    });

    // History rendering
    function addToHistory(equation, result) {
        history.unshift({ equation, result, time: new Date().toLocaleTimeString() });
        if (history.length > 20) history.pop();
        renderHistory();
    }

    function renderHistory() {
        if (history.length === 0) {
            historyList.innerHTML = '<div class="empty-state">No recent computations yet.</div>';
            return;
        }

        historyList.innerHTML = history.map(item => `
            <div class="history-item">
                <span class="history-eq">${item.equation}</span>
                <span class="history-res">${item.result}</span>
            </div>
        `).join('');
    }

    clearHistoryBtn.addEventListener('click', () => {
        history.length = 0;
        renderHistory();
        showToast('History cleared', 'success');
    });
}

/* ==========================================================================
   3. Text Studio Controller
   ========================================================================== */
function initTextStudio() {
    const textBox = document.getElementById('text-input-box');
    const statWords = document.getElementById('text-stat-words');
    const statChars = document.getElementById('text-stat-chars');
    const statPalindrome = document.getElementById('text-stat-palindrome');
    const outReversed = document.getElementById('text-out-reversed');
    const outTitleCase = document.getElementById('text-out-titlecase');
    const outRawJson = document.getElementById('text-out-rawjson');
    const textClearBtn = document.getElementById('text-clear-btn');
    const sampleBtns = document.querySelectorAll('.sample-text-btn');

    let debounceTimer = null;

    async function analyzeText() {
        const text = textBox.value;

        statChars.textContent = text.length;

        if (!text.trim()) {
            statWords.textContent = '0';
            statPalindrome.className = 'stat-badge is-not-palindrome';
            statPalindrome.textContent = 'Not Palindrome';
            outReversed.textContent = '—';
            outTitleCase.textContent = '—';
            outRawJson.textContent = '{ "ready": true }';
            return;
        }

        try {
            const response = await fetch('/api/text', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ operation: 'analyze', text: text })
            });

            if (!response.ok) return;

            const data = await response.json();

            statWords.textContent = data.wordCount;
            outReversed.textContent = data.reversed;
            outTitleCase.textContent = data.titleCase;
            outRawJson.textContent = JSON.stringify(data, null, 2);

            if (data.isPalindrome) {
                statPalindrome.className = 'stat-badge is-palindrome';
                statPalindrome.textContent = 'Palindrome';
            } else {
                statPalindrome.className = 'stat-badge is-not-palindrome';
                statPalindrome.textContent = 'Not Palindrome';
            }
        } catch (err) {
            console.error('Text analysis API failed:', err);
        }
    }

    textBox.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(analyzeText, 150);
    });

    sampleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            textBox.value = btn.dataset.text;
            analyzeText();
        });
    });

    textClearBtn.addEventListener('click', () => {
        textBox.value = '';
        analyzeText();
        textBox.focus();
    });

    // Copy to clipboard buttons
    document.querySelectorAll('.copy-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.dataset.target;
            const targetEl = document.getElementById(targetId);
            if (targetEl && targetEl.textContent && targetEl.textContent !== '—') {
                navigator.clipboard.writeText(targetEl.textContent).then(() => {
                    showToast('Copied to clipboard!', 'success');
                }).catch(() => {
                    showToast('Failed to copy', 'error');
                });
            }
        });
    });
}

/* ==========================================================================
   4. Telemetry & Server Health Check
   ========================================================================== */
function initTelemetry() {
    const statusPill = document.getElementById('system-status-pill');
    const statusText = document.getElementById('system-status-text');
    const telemStatus = document.getElementById('telem-status');
    const telemLatency = document.getElementById('telem-latency');
    const telemUptime = document.getElementById('telem-uptime');
    const refreshBtn = document.getElementById('refresh-telemetry-btn');

    async function checkHealth() {
        const start = performance.now();
        try {
            const res = await fetch('/api/health');
            const latency = Math.round(performance.now() - start);

            if (res.ok) {
                const data = await res.json();
                statusText.textContent = 'Server Live (Port 8080)';
                statusPill.style.background = 'rgba(16, 185, 129, 0.12)';
                statusPill.style.borderColor = 'rgba(16, 185, 129, 0.4)';
                statusPill.style.color = '#34d399';

                if (telemStatus) telemStatus.textContent = data.status || 'UP';
                if (telemLatency) telemLatency.textContent = `${latency} ms`;
                if (telemUptime) {
                    const secs = data.uptimeSeconds || 0;
                    const mins = Math.floor(secs / 60);
                    const remSecs = secs % 60;
                    telemUptime.textContent = mins > 0 ? `${mins}m ${remSecs}s` : `${remSecs}s`;
                }
            } else {
                throw new Error('Non-200 response');
            }
        } catch (err) {
            statusText.textContent = 'Server Disconnected';
            statusPill.style.background = 'rgba(239, 68, 68, 0.12)';
            statusPill.style.borderColor = 'rgba(239, 68, 68, 0.4)';
            statusPill.style.color = '#f87171';
            if (telemStatus) telemStatus.textContent = 'DOWN';
            if (telemLatency) telemLatency.textContent = '--';
        }
    }

    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            checkHealth();
            showToast('Telemetry refreshed', 'success');
        });
    }

    // Initial check and periodic heartbeat every 5 seconds
    checkHealth();
    setInterval(checkHealth, 5000);
}

/* ==========================================================================
   5. Toast Notification System
   ========================================================================== */
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
