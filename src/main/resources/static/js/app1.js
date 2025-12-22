// app1.js
document.addEventListener("DOMContentLoaded", () => {
  // If cached data exists, use it
  const cached = localStorage.getItem("financeData");
  if (cached) {
    try {
      const parsed = JSON.parse(cached);
      processLoadedData(parsed);
      return;
    } catch (e) {
      console.warn("Cached financeData corrupted, refetching.", e);
      localStorage.removeItem("financeData");
    }
  }

  // Fetch from backend endpoint (controller) that returns CSV-like keys:
  // Year, Month, Income, Running Expenses, Salaries, Trainer Share, Expenditure, Profit/Loss
  fetch("/api/finance/reports")
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch /api/finance/reports: " + res.status);
      return res.json();
    })
    .then(results => {
      // Save raw API response to cache (so future loads are instant)
      localStorage.setItem("financeData", JSON.stringify(results));
      // Process and render
      processLoadedData(results);
    })
    .catch(err => {
      console.error("Error loading finance reports:", err);
      // Optional fallback: try CSV if needed using Papa.parse
    });
});

// ----------------- Helpers -----------------
const parseMoney = (v) => {
  if (v === null || v === undefined) return 0;
  if (typeof v === "number") return v;
  // Some API returns strings like "₹1,234.56" or "1234.56"
  const cleaned = String(v).replace(/[₹, ]/g, "");
  const n = parseFloat(cleaned);
  return Number.isFinite(n) ? n : 0;
};

const safePercent = (num, denom, decimals = 1) => {
  if (!denom || denom === 0) return "0.0";
  return ((num / denom) * 100).toFixed(decimals);
};

function nextMonthNames(lastMonthName, count = 3) {
  const months = ["January","February","March","April","May","June","July","August","September","October","November","December"];
  const idx = months.findIndex(m => m.toLowerCase() === String(lastMonthName).toLowerCase());
  let start = idx >= 0 ? idx : (new Date()).getMonth();
  const out = [];
  for (let i = 1; i <= count; i++) out.push(months[(start + i) % 12]);
  return out;
}

// ----------------- Core processing -----------------
function processLoadedData(rawRows) {
  // rawRows is expected to be an array of objects with keys exactly like the CSV
  if (!Array.isArray(rawRows) || rawRows.length === 0) {
    console.warn("No data available in processLoadedData");
    return;
  }

  // Filter rows which have Month and Income defined
  const rows = rawRows.filter(r => r && r.Month && (r.Income !== undefined && r.Income !== null && String(r.Income).trim() !== ""));

  // Convert numeric fields and preserve variable names used elsewhere
  const cleanData = rows.map((row, i) => ({
    MonthNum: i + 1,
    Month: row.Month,
    Income: parseMoney(row.Income),
    RunningExpenses: parseMoney(row["Running Expenses"]),
    Salaries: parseMoney(row.Salaries),
    TrainerShare: parseMoney(row["Trainer Share"]),
    Expenditure: parseMoney(row.Expenditure),
    ProfitLoss: parseMoney(row["Profit/Loss"])
  }));

  // Debug sample
  console.log("Sample row (cleanData[0]):", cleanData[0]);

  // ---- Metrics ----
  const totalIncome = cleanData.reduce((s, r) => s + r.Income, 0);
  const totalExpenditure = cleanData.reduce((s, r) => s + r.Expenditure, 0);
  const netResult = cleanData.reduce((s, r) => s + r.ProfitLoss, 0);
  const avgIncome = cleanData.length ? Math.round(totalIncome / cleanData.length) : 0;
  const bestMonth = cleanData.reduce((a, b) => (a.ProfitLoss > b.ProfitLoss ? a : b), cleanData[0]);
  const worstMonth = cleanData.reduce((a, b) => (a.ProfitLoss < b.ProfitLoss ? a : b), cleanData[0]);

  // ---- Update DOM (safe updates: IDs if present, else nth-child fallback) ----
  const setByIdOrNth = (id, nth, selectorForValue, value, subtitle) => {
    let el = document.getElementById(id);
    if (el) {
      const mv = el.querySelector(selectorForValue || ".metric-value");
      if (mv) mv.textContent = value;
      if (subtitle) {
        const ms = el.querySelector(".metric-subtitle");
        if (ms) ms.textContent = subtitle;
      }
      return;
    }
    // fallback to nth-child (nth is 1-based)
    const fallback = document.querySelector(`.metric-card:nth-child(${nth})`);
    if (fallback) {
      const mv = fallback.querySelector(selectorForValue || ".metric-value");
      if (mv) mv.textContent = value;
      if (subtitle) {
        const ms = fallback.querySelector(".metric-subtitle");
        if (ms) ms.textContent = subtitle;
      }
    }
  };

  setByIdOrNth("metricTotalIncome", 1, ".metric-value", `₹${totalIncome.toLocaleString('en-IN')}`);
  setByIdOrNth("metricTotalExpenditure", 2, ".metric-value", `₹${totalExpenditure.toLocaleString('en-IN')}`);

  // Net result: also toggle class
  const netEl = document.getElementById("metricNetResult") || document.querySelector(".metric-card:nth-child(3)");
  if (netEl) {
    const mv = netEl.querySelector(".metric-value");
    if (mv) {
      mv.textContent = `₹${netResult.toLocaleString('en-IN')}`;
      mv.classList.remove("loss","success");
      mv.classList.add(netResult < 0 ? "loss" : "success");
    }
  }

  setByIdOrNth("metricAvgIncome", 4, ".metric-value", `₹${avgIncome.toLocaleString('en-IN')}`);
  setByIdOrNth("metricBestMonth", 5, ".metric-value", bestMonth.Month, `₹${bestMonth.ProfitLoss.toLocaleString('en-IN')} profit`);
  setByIdOrNth("metricWorstMonth", 6, ".metric-value", worstMonth.Month, `₹${worstMonth.ProfitLoss.toLocaleString('en-IN')} loss`);

  // ---- Charts + Table + Insights ----
  forecastProfitLoss(cleanData);
}

// ----------------- Forecasting & charts (keeps original variable names) -----------------
function forecastProfitLoss(data) {
  const x = data.map(r => r.MonthNum);
  const y = data.map(r => r.ProfitLoss);

  const n = x.length;
  const sumX = x.reduce((a,b) => a + b, 0);
  const sumY = y.reduce((a,b) => a + b, 0);
  const sumXY = x.reduce((a,b,i) => a + b * y[i], 0);
  const sumX2 = x.reduce((a,b) => a + b * b, 0);

  const denom = (n * sumX2 - sumX * sumX);
  const slope = denom !== 0 ? (n * sumXY - sumX * sumY) / denom : 0;
  const intercept = (sumY - slope * sumX) / n || 0;

  // Use dynamic next month names based on last real month
  const futureMonthNames = nextMonthNames(data[data.length - 1].Month, 3);
  const futureMonths = futureMonthNames.map((name, idx) => ({ name, num: n + idx + 1 }));
  const predictions = futureMonths.map(m => ({
    Month: m.name,
    ProfitLoss: Math.round(slope * m.num + intercept)
  }));

  // Update table (build HTML once)
  const tbody = document.getElementById("dataTableBody");
  if (tbody) {
    let out = "";
    data.forEach(r => {
      out += `
        <tr>
          <td>${r.Month}</td>
          <td>₹${r.Income.toLocaleString('en-IN')}</td>
          <td>₹${r.RunningExpenses.toLocaleString('en-IN')}</td>
          <td>₹${r.Salaries.toLocaleString('en-IN')}</td>
          <td>₹${r.TrainerShare.toLocaleString('en-IN')}</td>
          <td>₹${r.Expenditure.toLocaleString('en-IN')}</td>
          <td>${r.ProfitLoss >= 0 ? "₹" + r.ProfitLoss.toLocaleString('en-IN') : `<span style='color:red'>₹${r.ProfitLoss.toLocaleString('en-IN')}</span>`}</td>
          <td>${safePercent(r.ProfitLoss, r.Income, 1)}%</td>
        </tr>`;
    });

    predictions.forEach(p => {
      out += `
        <tr style="background:#f0f8ff; font-weight:bold;">
          <td>${p.Month} (Forecast)</td>
          <td colspan="5" style="text-align:center;">AI Predicted</td>
          <td>${p.ProfitLoss >= 0 ? "₹" + p.ProfitLoss.toLocaleString('en-IN') : `<span style='color:red'>₹${p.ProfitLoss.toLocaleString('en-IN')}</span>`}</td>
          <td>--</td>
        </tr>`;
    });

    tbody.innerHTML = out;
  }

  // Charts: align data lengths
  const labels = data.map(r => r.Month).concat(predictions.map(p => p.Month));
  const actualPLAll = data.map(r => r.ProfitLoss).concat(Array(predictions.length).fill(null));
  const forecastPLAll = Array(data.length).fill(null).concat(predictions.map(p => p.ProfitLoss));

  // Income vs Expenditure (line)
  try {
    const trendEl = document.getElementById("trendChart");
    if (trendEl) {
      new Chart(trendEl, {
        type: "line",
        data: {
          labels: data.map(r => r.Month),
          datasets: [
            { label: "Income", data: data.map(r => r.Income), borderColor: "green", fill: false },
            { label: "Expenditure", data: data.map(r => r.Expenditure), borderColor: "red", fill: false }
          ]
        },
        options: { responsive: true, plugins: { legend: { position: "top" } } }
      });
    }

    // Profit/Loss with forecast (bar)
    const plEl = document.getElementById("profitLossChart");
    if (plEl) {
      new Chart(plEl, {
        type: "bar",
        data: {
          labels,
          datasets: [
            { label: "Actual Profit/Loss", data: actualPLAll, backgroundColor: data.map(d => d.ProfitLoss >= 0 ? "green" : "red") },
            { label: "Forecast Profit/Loss", data: forecastPLAll, backgroundColor: "rgba(0,123,255,0.5)" }
          ]
        },
        options: { plugins: { legend: { position: "top" } }, responsive: true }
      });
    }

    // Profit/Loss forecast trend (line)
    const plTrendEl = document.getElementById("profitLossForecastChart");
    if (plTrendEl) {
      new Chart(plTrendEl, {
        type: "line",
        data: {
          labels,
          datasets: [
            { label: "Actual Profit/Loss", data: data.map(r => r.ProfitLoss).concat(Array(predictions.length).fill(null)), borderColor: "green", fill: true, tension: 0.3 },
            { label: "Forecast Profit/Loss", data: Array(data.length).fill(null).concat(predictions.map(p => p.ProfitLoss)), borderColor: "blue", fill: true, borderDash: [5,5], tension: 0.3 }
          ]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { position: "top" },
            tooltip: { callbacks: { label: ctx => ctx.raw !== null ? `₹${ctx.raw.toLocaleString('en-IN')}` : "" } }
          },
          scales: { y: { title: { display: true, text: "Profit/Loss (₹)" }, ticks: { callback: val => "₹" + Number(val).toLocaleString('en-IN') } } }
        }
      });
    }

    // Expense breakdown (stacked)
    const expEl = document.getElementById("expenseBreakdownChart");
    if (expEl) {
      new Chart(expEl, {
        type: "bar",
        data: {
          labels: data.map(r => r.Month),
          datasets: [
            { label: "Running Expenses", data: data.map(r => r.RunningExpenses), backgroundColor: "rgba(255, 99, 132, 0.7)" },
            { label: "Salaries", data: data.map(r => r.Salaries), backgroundColor: "rgba(54, 162, 235, 0.7)" },
            { label: "Trainer Share", data: data.map(r => r.TrainerShare), backgroundColor: "rgba(255, 206, 86, 0.7)" }
          ]
        },
        options: { responsive: true, plugins: { legend: { position: "top" } }, scales: { x: { stacked: true }, y: { stacked: true, title: { display: true, text: "Expenses (₹)" } } } }
      });
    }

    // Expense distribution (doughnut)
    const distEl = document.getElementById("expenseDistributionChart");
    if (distEl) {
      const avgRunning = data.reduce((s, r) => s + r.RunningExpenses, 0) / data.length;
      const avgSalaries = data.reduce((s, r) => s + r.Salaries, 0) / data.length;
      const avgTrainer = data.reduce((s, r) => s + r.TrainerShare, 0) / data.length;

      new Chart(distEl, {
        type: "doughnut",
        data: { labels: ["Running Expenses","Salaries","Trainer Share"], datasets: [{ data: [avgRunning, avgSalaries, avgTrainer], backgroundColor: ["rgba(255,99,132,0.7)","rgba(54,162,235,0.7)","rgba(255,206,86,0.7)"] }] },
        options: { responsive: true, plugins: { legend: { position: "bottom" }, tooltip: { callbacks: { label: ctx => `₹${ctx.raw.toLocaleString('en-IN')}` } } } }
      });
    }
  } catch (e) {
    console.error("Chart rendering error:", e);
  }

  // Expense summary update
  const totalRunning = data.reduce((s, r) => s + r.RunningExpenses, 0);
  const totalSalaries = data.reduce((s, r) => s + r.Salaries, 0);
  const totalTrainer = data.reduce((s, r) => s + r.TrainerShare, 0);
  const expenseItems = document.querySelectorAll(".expense-summary .expense-item .expense-amount");
  if (expenseItems.length >= 3) {
    expenseItems[0].textContent = `₹${totalRunning.toLocaleString('en-IN')}`;
    expenseItems[1].textContent = `₹${totalSalaries.toLocaleString('en-IN')}`;
    expenseItems[2].textContent = `₹${totalTrainer.toLocaleString('en-IN')}`;
  }

  // Insights
  generateInsights(data, predictions);
}

// ----------------- Insights -----------------
function generateInsights(data, predictions) {
  const insightsPanel = document.getElementById("insightsPanel");
  if (!insightsPanel) return;
  insightsPanel.innerHTML = "";

  const lastIncome = data[data.length - 1].Income || 0;
  const firstIncome = data[0].Income || 0;
  const incomeDrop = firstIncome ? Math.round(((firstIncome - lastIncome) / firstIncome) * 100) : 0;

  const maxExpense = Math.max(...data.map(r => r.RunningExpenses));
  const avgExpense = data.reduce((s, r) => s + r.RunningExpenses, 0) / data.length;
  const bestMonth = data.reduce((a, b) => (a.ProfitLoss > b.ProfitLoss ? a : b));
  const worstMonth = data.reduce((a, b) => (a.ProfitLoss < b.ProfitLoss ? a : b));

  const addInsight = (type, icon, title, message) => {
    insightsPanel.innerHTML += `
      <div class="insight-card ${type}">
        <div class="insight-icon"><i class="fas ${icon}"></i></div>
        <div class="insight-content">
          <h3>${title}</h3>
          <p>${message}</p>
        </div>
      </div>`;
  };

  if (incomeDrop > 20) addInsight("critical","fa-arrow-trend-down","Income Decline",
    `Income dropped by ${incomeDrop}% from ${data[0].Month} (₹${firstIncome.toLocaleString('en-IN')}) to ${data[data.length - 1].Month} (₹${lastIncome.toLocaleString('en-IN')}).`);

  if (maxExpense > avgExpense * 1.5) addInsight("warning","fa-chart-bar","Expense Volatility",
    `Running expenses peaked at ₹${maxExpense.toLocaleString('en-IN')}, indicating volatility.`);

  addInsight("success","fa-trophy","Best Month", `${bestMonth.Month} with profit of ₹${bestMonth.ProfitLoss.toLocaleString('en-IN')}.`);
  addInsight("warning","fa-exclamation-triangle","Worst Month", `${worstMonth.Month} with loss of ₹${worstMonth.ProfitLoss.toLocaleString('en-IN')}.`);

  const futureTrend = predictions.map(p => p.ProfitLoss);
  if (futureTrend.every(v => v > 0)) addInsight("success","fa-lightbulb","Positive Outlook","AI forecast shows profits in all upcoming months.");
  else if (futureTrend.every(v => v < 0)) addInsight("critical","fa-bolt","Negative Forecast","AI forecast predicts continued losses.");
  else addInsight("info","fa-balance-scale","Mixed Forecast","AI forecast suggests mixed results; focus on stabilizing revenue.");
}
