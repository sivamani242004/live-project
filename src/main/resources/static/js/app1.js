// app.js
document.addEventListener("DOMContentLoaded", () => {
    Papa.parse("financial_data.csv", {
        download: true,
        header: true,
        complete: function(results) {
            const data = results.data.filter(row => row.Month && row.Income);

            // Convert numeric fields
            const cleanData = data.map((row, i) => ({
                MonthNum: i + 1,
                Month: row.Month,
                Income: parseInt(row.Income.replace(/[₹, ]/g, "")) || 0,
                RunningExpenses: parseInt(row["Running Expenses"]?.replace(/[₹, ]/g, "")) || 0,
                Salaries: parseInt(row.Salaries?.replace(/[₹, ]/g, "")) || 0,
                TrainerShare: parseInt(row["Trainer Share"]?.replace(/[₹, ]/g, "")) || 0,
                Expenditure: parseInt(row.Expenditure?.replace(/[₹, ]/g, "")) || 0,
                ProfitLoss: parseInt(row["Profit/Loss"]?.replace(/[₹, ]/g, "")) || 0
            }));

            console.log("Sample row after cleaning:", cleanData[0]); // debug

            // ---- Metrics ----
            const totalIncome = cleanData.reduce((s, r) => s + r.Income, 0);
            const totalExpenditure = cleanData.reduce((s, r) => s + r.Expenditure, 0);
            const netResult = cleanData.reduce((s, r) => s + r.ProfitLoss, 0);
            const avgIncome = (totalIncome / cleanData.length).toFixed(0);
            const bestMonth = cleanData.reduce((a, b) => (a.ProfitLoss > b.ProfitLoss ? a : b));
            const worstMonth = cleanData.reduce((a, b) => (a.ProfitLoss < b.ProfitLoss ? a : b));

            // Update DOM
            document.querySelector(".metric-card:nth-child(1) .metric-value").textContent = `₹${totalIncome.toLocaleString()}`;
            document.querySelector(".metric-card:nth-child(2) .metric-value").textContent = `₹${totalExpenditure.toLocaleString()}`;
            const netResultCard = document.querySelector(".metric-card:nth-child(3) .metric-value");
            netResultCard.textContent = `₹${netResult.toLocaleString()}`;
            if (netResult < 0) {
                netResultCard.classList.add("loss");
            } else {
                netResultCard.classList.add("success");
            }
            document.querySelector(".metric-card:nth-child(4) .metric-value").textContent = `₹${parseInt(avgIncome).toLocaleString()}`;
            document.querySelector(".metric-card:nth-child(5) .metric-value").textContent = bestMonth.Month;
            document.querySelector(".metric-card:nth-child(5) .metric-subtitle").textContent = `₹${bestMonth.ProfitLoss.toLocaleString()} profit`;
            document.querySelector(".metric-card:nth-child(6) .metric-value").textContent = worstMonth.Month;
            document.querySelector(".metric-card:nth-child(6) .metric-subtitle").textContent = `₹${worstMonth.ProfitLoss.toLocaleString()} loss`;

            // Charts + Table + Insights
            forecastProfitLoss(cleanData);
        }
    });
});

// ---- Forecasting & Chart ----
function forecastProfitLoss(data) {
    const x = data.map(r => r.MonthNum);
    const y = data.map(r => r.ProfitLoss);

    // Regression
    const n = x.length;
    const sumX = x.reduce((a, b) => a + b, 0);
    const sumY = y.reduce((a, b) => a + b, 0);
    const sumXY = x.reduce((a, b, i) => a + b * y[i], 0);
    const sumX2 = x.reduce((a, b) => a + b * b, 0);
    const slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    const intercept = (sumY - slope * sumX) / n;

    // Predictions
    const futureMonths = [
        { name: "October", num: n + 1 },
        { name: "November", num: n + 2 },
        { name: "December", num: n + 3 }
    ];
    const predictions = futureMonths.map(m => ({
        Month: m.name,
        ProfitLoss: Math.round(slope * m.num + intercept)
    }));

    // Update table
    const tbody = document.getElementById("dataTableBody");
    tbody.innerHTML = "";
    data.forEach(r => {
        tbody.innerHTML += `
            <tr>
                <td>${r.Month}</td>
                <td>₹${r.Income.toLocaleString()}</td>
                <td>₹${r.RunningExpenses.toLocaleString()}</td>
                <td>₹${r.Salaries.toLocaleString()}</td>
                <td>₹${r.TrainerShare.toLocaleString()}</td>
                <td>₹${r.Expenditure.toLocaleString()}</td>
                <td>${r.ProfitLoss >= 0 ? "₹" + r.ProfitLoss.toLocaleString() : `<span style='color:red'>₹${r.ProfitLoss.toLocaleString()}</span>`}</td>
                <td>${((r.ProfitLoss / r.Income) * 100).toFixed(1)}%</td>
            </tr>
        `;
    });
    predictions.forEach(p => {
        tbody.innerHTML += `
            <tr style="background:#f0f8ff; font-weight:bold;">
                <td>${p.Month} (Forecast)</td>
                <td colspan="5" style="text-align:center;">AI Predicted</td>
                <td>${p.ProfitLoss >= 0 ? "₹" + p.ProfitLoss.toLocaleString() : `<span style='color:red'>₹${p.ProfitLoss.toLocaleString()}</span>`}</td>
                <td>--</td>
            </tr>
        `;
    });

    // ---- Charts ----
    const months = data.map(r => r.Month).concat(predictions.map(p => p.Month));

    // Income vs Expenditure
    new Chart(document.getElementById("trendChart"), {
        type: "line",
        data: {
            labels: data.map(r => r.Month),
            datasets: [
                { label: "Income", data: data.map(r => r.Income), borderColor: "green", fill: false },
                { label: "Expenditure", data: data.map(r => r.Expenditure), borderColor: "red", fill: false }
            ]
        }
    });

    // Profit/Loss with Forecast (bar chart)
    new Chart(document.getElementById("profitLossChart"), {
        type: "bar",
        data: {
            labels: months,
            datasets: [
                {
                    label: "Actual Profit/Loss",
                    data: data.map(r => r.ProfitLoss),
                    backgroundColor: data.map(v => v >= 0 ? "green" : "red")
                },
                {
                    label: "Forecast Profit/Loss",
                    data: predictions.map(p => p.ProfitLoss),
                    backgroundColor: "rgba(0,123,255,0.5)"
                }
            ]
        },
        options: {
            plugins: { legend: { position: "top" } }
        }
    });

    // Profit/Loss Forecast Trend (line chart)
    new Chart(document.getElementById("profitLossForecastChart"), {
        type: "line",
        data: {
            labels: months,
            datasets: [
                {
                    label: "Actual Profit/Loss",
                    data: data.map(r => r.ProfitLoss).concat(Array(predictions.length).fill(null)),
                    borderColor: "green",
                    backgroundColor: "rgba(0,128,0,0.2)",
                    fill: true,
                    tension: 0.3
                },
                {
                    label: "Forecast Profit/Loss",
                    data: Array(data.length).fill(null).concat(predictions.map(p => p.ProfitLoss)),
                    borderColor: "blue",
                    backgroundColor: "rgba(0,123,255,0.2)",
                    fill: true,
                    borderDash: [5, 5],
                    tension: 0.3
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" },
                tooltip: {
                    callbacks: {
                        label: (ctx) => ctx.raw !== null ? `₹${ctx.raw.toLocaleString()}` : ""
                    }
                }
            },
            scales: {
                y: {
                    title: { display: true, text: "Profit/Loss (₹)" },
                    ticks: { callback: (val) => "₹" + val.toLocaleString() }
                }
            }
        }
    });

    // ---- Monthly Expense Breakdown (stacked bar chart) ----
    new Chart(document.getElementById("expenseBreakdownChart"), {
        type: "bar",
        data: {
            labels: data.map(r => r.Month),
            datasets: [
                {
                    label: "Running Expenses",
                    data: data.map(r => r.RunningExpenses),
                    backgroundColor: "rgba(255, 99, 132, 0.7)"
                },
                {
                    label: "Salaries",
                    data: data.map(r => r.Salaries),
                    backgroundColor: "rgba(54, 162, 235, 0.7)"
                },
                {
                    label: "Trainer Share",
                    data: data.map(r => r.TrainerShare),
                    backgroundColor: "rgba(255, 206, 86, 0.7)"
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" },
                tooltip: {
                    callbacks: {
                        label: (ctx) => `₹${ctx.raw.toLocaleString()}`
                    }
                }
            },
            scales: {
                x: { stacked: true },
                y: { stacked: true, title: { display: true, text: "Expenses (₹)" } }
            }
        }
    });

    // ---- Average Expense Distribution (doughnut chart) ----
const avgRunning = data.reduce((s, r) => s + r.RunningExpenses, 0) / data.length;
const avgSalaries = data.reduce((s, r) => s + r.Salaries, 0) / data.length;
const avgTrainer = data.reduce((s, r) => s + r.TrainerShare, 0) / data.length;

new Chart(document.getElementById("expenseDistributionChart"), {
    type: "doughnut",
    data: {
        labels: ["Running Expenses", "Salaries", "Trainer Share"],
        datasets: [
            {
                data: [avgRunning, avgSalaries, avgTrainer],
                backgroundColor: [
                    "rgba(255, 99, 132, 0.7)",   // Running Expenses
                    "rgba(54, 162, 235, 0.7)",  // Salaries
                    "rgba(255, 206, 86, 0.7)"   // Trainer Share
                ],
                borderWidth: 1
            }
        ]
    },
    options: {
        responsive: true,
        plugins: {
            legend: { position: "bottom" },
            tooltip: {
                callbacks: {
                    label: (ctx) => `₹${ctx.raw.toLocaleString()}`
                }
            }
        }
    }
});

   // ---- Expense Summary (dynamic update) ----
    const totalRunning = data.reduce((s, r) => s + r.RunningExpenses, 0);
    const totalSalaries = data.reduce((s, r) => s + r.Salaries, 0);
    const totalTrainer = data.reduce((s, r) => s + r.TrainerShare, 0);

    const expenseItems = document.querySelectorAll(".expense-summary .expense-item .expense-amount");
    if (expenseItems.length >= 3) {
        expenseItems[0].textContent = `₹${totalRunning.toLocaleString()}`;
        expenseItems[1].textContent = `₹${totalSalaries.toLocaleString()}`;
        expenseItems[2].textContent = `₹${totalTrainer.toLocaleString()}`;
    }

    generateInsights(data, predictions);
}

// ---- Insights ----
function generateInsights(data, predictions) {
    const insightsPanel = document.getElementById("insightsPanel");
    insightsPanel.innerHTML = "";

    const lastIncome = data[data.length - 1].Income;
    const firstIncome = data[0].Income;
    const incomeDrop = (((firstIncome - lastIncome) / firstIncome) * 100).toFixed(0);

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
            </div>
        `;
    };

    // Critical: Income decline
    if (incomeDrop > 20) {
        addInsight(
            "critical",
            "fa-arrow-trend-down",
            "Income Decline",
            `Income dropped by ${incomeDrop}% from ${data[0].Month} (₹${firstIncome.toLocaleString()}) to ${data[data.length - 1].Month} (₹${lastIncome.toLocaleString()}).`
        );
    }

    // Expense volatility
    if (maxExpense > avgExpense * 1.5) {
        addInsight(
            "warning",
            "fa-chart-bar",
            "Expense Volatility",
            `Running expenses peaked at ₹${maxExpense.toLocaleString()}, indicating volatility that needs stricter budgeting.`
        );
    }

    // Best & Worst month
    addInsight("success", "fa-trophy", "Best Month", `${bestMonth.Month} with profit of ₹${bestMonth.ProfitLoss.toLocaleString()}.`);
    addInsight("warning", "fa-exclamation-triangle", "Worst Month", `${worstMonth.Month} with loss of ₹${worstMonth.ProfitLoss.toLocaleString()}.`);

    // Forecast insights
    const futureTrend = predictions.map(p => p.ProfitLoss);
    if (futureTrend.every(v => v > 0)) {
        addInsight("success", "fa-lightbulb", "Positive Outlook", "AI forecast shows profits in all upcoming months. Continue cost control + marketing.");
    } else if (futureTrend.every(v => v < 0)) {
        addInsight("critical", "fa-bolt", "Negative Forecast", "AI forecast predicts continued losses — urgent action required to boost revenue.");
    } else {
        addInsight("info", "fa-balance-scale", "Mixed Forecast", "AI forecast suggests alternating profit/loss in coming months. Focus on stabilizing revenues.");
    }
}
