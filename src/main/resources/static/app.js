(function () {
  "use strict";

  const state = {
    authHeader: sessionStorage.getItem("authHeader") || null,
    username: sessionStorage.getItem("username") || null,
    role: sessionStorage.getItem("role") || null,
    currentGameId: null,
  };

  // ---------- DOM references ----------
  const authScreen = document.getElementById("authScreen");
  const gameScreen = document.getElementById("gameScreen");
  const adminScreen = document.getElementById("adminScreen");
  const userBadge = document.getElementById("userBadge");
  const userLabel = document.getElementById("userLabel");

  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");
  const loginMsg = document.getElementById("loginMsg");
  const registerMsg = document.getElementById("registerMsg");

  const newGameBtn = document.getElementById("newGameBtn");
  const board = document.getElementById("board");
  const guessForm = document.getElementById("guessForm");
  const guessInput = document.getElementById("guessInput");
  const gameMsg = document.getElementById("gameMsg");

  const reportForm = document.getElementById("reportForm");
  const reportDate = document.getElementById("reportDate");
  const reportResult = document.getElementById("reportResult");
  const reportMsg = document.getElementById("reportMsg");

  const modal = document.getElementById("modal");
  const modalText = document.getElementById("modalText");
  const modalOk = document.getElementById("modalOk");

  // ---------- API helper ----------
  async function api(path, options) {
    options = options || {};
    const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
    if (state.authHeader) {
      headers["Authorization"] = state.authHeader;
    }
    const res = await fetch(path, Object.assign({}, options, { headers }));
    let data = null;
    try {
      data = await res.json();
    } catch (e) {
      data = null;
    }
    if (!res.ok) {
      const message = (data && data.error) || "Something went wrong (" + res.status + ").";
      throw new Error(message);
    }
    return data;
  }

  function setMsg(el, text, isError) {
    el.textContent = text || "";
    el.className = "form-msg " + (isError ? "error" : "success");
  }

  // ---------- Auth tabs ----------
  document.querySelectorAll(".tab-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
      const tab = btn.dataset.tab;
      loginForm.classList.toggle("hidden", tab !== "login");
      registerForm.classList.toggle("hidden", tab !== "register");
    });
  });

  // ---------- Register ----------
  registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMsg(registerMsg, "", false);
    const username = document.getElementById("regUsername").value.trim();
    const password = document.getElementById("regPassword").value;
    try {
      const data = await api("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({ username, password }),
      });
      setMsg(registerMsg, data.message || "Registered! You can now log in.", false);
      registerForm.reset();
    } catch (err) {
      setMsg(registerMsg, err.message, true);
    }
  });

  // ---------- Login ----------
  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMsg(loginMsg, "", false);
    const username = document.getElementById("loginUsername").value.trim();
    const password = document.getElementById("loginPassword").value;
    const header = "Basic " + btoa(username + ":" + password);

    try {
      const res = await fetch("/api/auth/me", { headers: { Authorization: header } });
      if (!res.ok) {
        throw new Error("Invalid username or password.");
      }
      const data = await res.json();
      state.authHeader = header;
      state.username = data.username;
      state.role = data.role;
      sessionStorage.setItem("authHeader", header);
      sessionStorage.setItem("username", data.username);
      sessionStorage.setItem("role", data.role);
      loginForm.reset();
      renderForRole();
    } catch (err) {
      setMsg(loginMsg, err.message, true);
    }
  });

  // ---------- Logout ----------
  document.getElementById("logoutBtn").addEventListener("click", () => {
    state.authHeader = null;
    state.username = null;
    state.role = null;
    state.currentGameId = null;
    sessionStorage.clear();
    renderForRole();
  });

  // ---------- Screen switching ----------
  function renderForRole() {
    const loggedIn = !!state.authHeader;
    authScreen.classList.toggle("hidden", loggedIn);
    userBadge.classList.toggle("hidden", !loggedIn);
    gameScreen.classList.add("hidden");
    adminScreen.classList.add("hidden");

    if (!loggedIn) return;

    userLabel.textContent = state.username + " (" + state.role + ")";

    if (state.role === "ADMIN") {
      adminScreen.classList.remove("hidden");
      reportDate.value = new Date().toISOString().slice(0, 10);
    } else {
      gameScreen.classList.remove("hidden");
      board.innerHTML = "";
      guessForm.classList.add("hidden");
      setMsg(gameMsg, "", false);
    }
  }

  // ---------- Game rendering ----------
  function renderBoard(guesses, guessesRemaining) {
    board.innerHTML = "";
    const totalRows = 5;
    for (let r = 0; r < totalRows; r++) {
      const row = document.createElement("div");
      row.className = "board-row";
      const guess = guesses[r];
      for (let c = 0; c < 5; c++) {
        const tile = document.createElement("div");
        if (guess) {
          tile.className = "tile " + guess.pattern[c];
          tile.textContent = guess.guessText[c];
        } else {
          tile.className = "tile empty";
        }
        row.appendChild(tile);
      }
      board.appendChild(row);
    }
  }

  function showModal(text) {
    modalText.textContent = text;
    modal.classList.remove("hidden");
  }
  modalOk.addEventListener("click", () => {
    modal.classList.add("hidden");
    guessForm.classList.add("hidden");
  });

  // ---------- Start new game ----------
  newGameBtn.addEventListener("click", async () => {
    setMsg(gameMsg, "", false);
    try {
      const data = await api("/api/game/start", { method: "POST" });
      state.currentGameId = data.gameId;
      renderBoard(data.guesses, data.guessesRemaining);
      guessForm.classList.remove("hidden");
      guessInput.value = "";
      guessInput.focus();
    } catch (err) {
      setMsg(gameMsg, err.message, true);
    }
  });

  // ---------- Submit guess ----------
  guessForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMsg(gameMsg, "", false);
    const guess = guessInput.value.trim().toUpperCase();
    if (!/^[A-Z]{5}$/.test(guess)) {
      setMsg(gameMsg, "Enter exactly 5 letters (A-Z).", true);
      return;
    }
    try {
      const data = await api("/api/game/" + state.currentGameId + "/guess", {
        method: "POST",
        body: JSON.stringify({ guess }),
      });
      renderBoard(data.guesses, data.guessesRemaining);
      guessInput.value = "";

      if (data.status === "WON") {
        showModal(data.message || "Congratulations! You guessed the word correctly!");
      } else if (data.status === "LOST") {
        showModal(data.message || "Better luck next time!");
      } else {
        guessInput.focus();
      }
    } catch (err) {
      setMsg(gameMsg, err.message, true);
    }
  });

  // ---------- Admin report ----------
  reportForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMsg(reportMsg, "", false);
    reportResult.classList.add("hidden");
    try {
      const date = reportDate.value;
      const data = await api("/api/admin/report?date=" + date);
      document.getElementById("statUsers").textContent = data.numberOfUsers;
      document.getElementById("statGames").textContent = data.numberOfGamesPlayed;
      document.getElementById("statWins").textContent = data.numberOfCorrectGuesses;
      reportResult.classList.remove("hidden");
    } catch (err) {
      setMsg(reportMsg, err.message, true);
    }
  });

  // ---------- Admin user report ----------
  const userReportForm = document.getElementById("userReportForm");
  const userReportTable = document.getElementById("userReportTable");
  const userReportBody = document.getElementById("userReportBody");
  const userReportMsg = document.getElementById("userReportMsg");

  userReportForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    setMsg(userReportMsg, "", false);
    userReportTable.classList.add("hidden");
    userReportBody.innerHTML = "";
    const username = document.getElementById("userReportUsername").value.trim();
    const date = document.getElementById("userReportDate").value;
    let url = "/api/admin/user-report?username=" + encodeURIComponent(username);
    if (date) {
      url += "&date=" + date;
    }
    try {
      const data = await api(url);
      if (!data.rows.length) {
        setMsg(userReportMsg, "No games found for " + data.username + ".", false);
        return;
      }
      data.rows.forEach((r) => {
        const tr = document.createElement("tr");
        [r.date, r.wordsTried, r.correctGuesses].forEach((v) => {
          const td = document.createElement("td");
          td.textContent = v;
          tr.appendChild(td);
        });
        userReportBody.appendChild(tr);
      });
      userReportTable.classList.remove("hidden");
    } catch (err) {
      setMsg(userReportMsg, err.message, true);
    }
  });

  // ---------- Init ----------
  renderForRole();
})();
